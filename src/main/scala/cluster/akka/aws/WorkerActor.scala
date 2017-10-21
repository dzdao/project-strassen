package cluster.akka.aws

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import scala.concurrent.duration._
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
  var workerID = message
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

          temp += a(j) * b(j)(i)
        }
        //println(temp)
        result(i) = temp
      }

      listenerActor ! DoneMsg(s"Worker #$workerID is done")
      listenerActor ! CompletedWork(message, result)

  }
}
