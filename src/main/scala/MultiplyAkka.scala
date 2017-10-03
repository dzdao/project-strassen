import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
//import akka.routing.RoundRobin
import scala.io.StdIn
import scala.util.Random

/** worker companion object */
object Worker {
  def props(message: Int, listenerActor: ActorRef)
  : Props = Props(new Worker(message, listenerActor))

  final case class WhatToMultiply(matrix: Array[Int])
  case object ExecuteMultiply
}

/** worker actor */
class Worker(message: Int, listenerActor: ActorRef) extends Actor {
  import Worker._
  import Listener._

  var workerMatrix = Array[Int]()

  def receive = {
    case WhatToMultiply(matrix) =>
      workerMatrix = matrix
    case ExecuteMultiply =>
      for (i <- 0 until workerMatrix.length) {
        print(workerMatrix(i) + " ")
      }
      println()
      listenerActor ! DoneMsg(s"Worker #$message is done")
  }
}

/** listener companion object */
object Listener {
  def props: Props = Props[Listener]
  final case class DoneMsg(message: String)
}

/** listener actor */
class Listener extends Actor {
  import Listener._

  var result = Array[Array[Int]]()

  def receive = {
    case DoneMsg(message) => println(message)

  }
}

/** main class */
object MultiplyAkka extends App {
  import Worker._
  import scala.collection.mutable.Map

  def generateMatrix(dim: Int): Array[Array[Int]] = {
    val rand = Random
    val matrix = Array.ofDim[Int](dim, dim)

    for(i <- 0 until dim) {
      for(j <- 0 until dim) {
        matrix(i)(j) = rand.nextInt(100)
      }
    }
    matrix
  }

  def printMatrix(matrix: Array[Array[Int]]): Unit = {
    for {
      i <- 0 until matrix.length
      j <- 0 until matrix.length
    } println(s"($i)($j) = ${matrix(i)(j)}")
  }

  // set the matrix dimension
  val dim = 4
  val a = generateMatrix(dim)
  val b = generateMatrix(dim)

  // allocate memory for resultant matrix
  val c = Array.ofDim[Int](dim, dim)


  // create the container to hold actors
  val system: ActorSystem = ActorSystem("MultiplyAkka")

  try {
    val numOfActors = a.length
    var actorRefs = Map[Int, ActorRef]()
    val listener: ActorRef = system.actorOf(Listener.props, "ListenerActor")

    for(row <- 0 until numOfActors) {

      // map row key -> reference to specific worker
      actorRefs += (row -> system.actorOf(Worker.props(row, listener), s"Worker-$row"))

      // send matrix row and a trigger message to start the task
      actorRefs(row) ! WhatToMultiply(a(row))
      actorRefs(row) ! ExecuteMultiply
    }

    println("Press enter to terminate program")
    StdIn.readLine()

  } finally {
    system.terminate()
  }
}
