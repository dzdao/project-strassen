package cluster.akka.aws

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import scala.concurrent.duration._
import scala.io.StdIn
import scala.util.Random

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
  val dim = 3000
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
    val listener: ActorRef = system.actorOf(Listener.props(a.length), "ListenerActor")

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
