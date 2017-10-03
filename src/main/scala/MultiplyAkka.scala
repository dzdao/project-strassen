import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import scala.io.StdIn
import scala.util.Random

// worker companion object
object Worker {
  def props(message: Int, listenerActor: ActorRef)
  : Props = Props(new Worker(message, listenerActor))

  final case class WhatToMultiply(matrix: Array[Int])
  case object ExecuteMultiply
}

// worker actor
class Worker(message: Int, listenerActor: ActorRef) extends Actor {
  import Worker._

  var workerMatrix = Array[Int]()

  def receive = {
    case WhatToMultiply(matrix) =>
      workerMatrix = matrix
    case ExecuteMultiply =>
      for (i <- 0 until workerMatrix.length) {
        println(workerMatrix(i))
      }
  }
}


object MultiplyAkka extends App {

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
  val dim = 2
  val a = generateMatrix(dim)
  val b = generateMatrix(dim)

  // allocate memory for resultant matrix
  val c = Array.ofDim[Int](dim, dim)


  // create the container to hold actors
  val system: ActorSystem = ActorSystem("MultiplyAkka")

  try {




    println("Press enter to terminate program")
    StdIn.readLine()

  } finally {
    system.terminate()
  }
}
