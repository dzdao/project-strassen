import scala.util.Random
import akka.actor._

object Strassen {

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

  def main(args: Array[String]): Unit = {
    println("Hello World!")
    val dim = 100

    val randMatrix = generateMatrix(dim)
    printMatrix(randMatrix)

  }
}

