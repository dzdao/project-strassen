import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
//import akka.routing.RoundRobin
import scala.io.StdIn
import scala.util.Random

/** worker companion object */
object Worker {
  def props(message: Int, listenerActor: ActorRef)
  : Props = Props(new Worker(message, listenerActor))

  final case class WhatToMultiply(matrixA: Array[Int], matrixB: Array[Array[Int]])
  case object ExecuteMultiply
}

/** worker actor */
class Worker(message: Int, listenerActor: ActorRef) extends Actor {
  import Worker._
  import Listener._

  var a = Array[Int]()
  var b = Array[Array[Int]]()
  var result = Array[Int]()

  def receive = {
    case WhatToMultiply(matrixA, matrixB) =>
      a = matrixA
      b = matrixB
      result = Array.ofDim[Int](a.length)

    case ExecuteMultiply =>
      for (i <- 0 until a.length) {
        var temp = 0
        for (j <- 0 until b.length) {

          temp += a(i) * b(i)(j)
        }
        result(i) = temp
      }

      listenerActor ! DoneMsg(s"Worker #$message is done")
      listenerActor ! CompletedWork(message, result)

  }
}

/** listener companion object */
object Listener {
  def props: Props = Props[Listener]
  final case class DoneMsg(message: String)
  final case class CompletedWork(row: Int, work: Array[Int])
}

/** listener actor */
class Listener extends Actor {
  import Listener._

  var result = Array[Array[Int]]()

  def receive = {
    case DoneMsg(message) => println(message)
    case CompletedWork(row, work) =>
      result(row) = work
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
  val dim = 10
  val a = generateMatrix(dim)
  val b = generateMatrix(dim)

  // allocate memory for resultant matrix
  val c = Array.ofDim[Int](dim, dim)


  // create the container to hold all the actors
  val system: ActorSystem = ActorSystem("MultiplyAkka")

  try {
    val numOfActors = a.length
    var actorRefs = Map[Int, ActorRef]()

    // listener actor will receive messages from all worker actors
    val listener: ActorRef = system.actorOf(Listener.props, "ListenerActor")

    // generate worker actors
    for(row <- 0 until numOfActors) {

      // map row key -> reference to specific worker
      actorRefs += (row -> system.actorOf(Worker.props(row, listener), s"Worker-$row"))

      // distribute workload and send a trigger message to start
      actorRefs(row) ! WhatToMultiply(a(row), b)
      actorRefs(row) ! ExecuteMultiply
    }

    println("Press enter to terminate program")
    StdIn.readLine()

  } finally {
    system.terminate()
  }
}
