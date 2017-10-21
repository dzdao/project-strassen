package cluster.akka.aws

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random

/** listener companion object */
object Listener {
  def props(message: Int): Props = Props(new Listener(message))
  final case class DoneMsg(message: String)
  final case class CompletedWork(row: Int, work: Array[Int])
}

/** listener actor */
class Listener(message: Int) extends Actor {
  import Listener._

  val startTime: Long = System.currentTimeMillis
  var resultMatrix = Array.ofDim[Array[Int]](message)
  var numOfRows = 0

  def receive = {
    //case DoneMsg(message) => println(message)
    case CompletedWork(row, work) =>
      resultMatrix(row) = work
      numOfRows += 1
      if(numOfRows == resultMatrix.length) {
        println("All workers done. Calculation time: %s"
          .format((System.currentTimeMillis - startTime).millis))

//        for {
//          i <- 0 until resultMatrix.length
//          j <- 0 until resultMatrix.length
//        } print(s"${resultMatrix(i)(j)}\n")
      }
  }
}
