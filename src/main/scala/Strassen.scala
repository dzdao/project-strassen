import scala.util.Random
import akka.actor._

object Strassen {

  def naiveMultiply(a: Array[Array[Int]], b: Array[Array[Int]]): Array[Array[Int]] = {
    val result = Array.ofDim[Int](a.length, b.length)

    for(i <- 0 until a.length) {
      for(j <- 0 until a.length) {
        var element = 0

        for(k <- 0 until a.length) {
          var product = a(i)(k) * b(k)(j)
          element += product
        }

        result(i)(j) = element
      }
    }
    result
  }

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

    val dim = 2

    val a = generateMatrix(dim)
    printMatrix(a)
    println()

    val b = generateMatrix(dim)
    printMatrix(b)
    println()

    val c = naiveMultiply(a, b)
    printMatrix(c)
  }
}

