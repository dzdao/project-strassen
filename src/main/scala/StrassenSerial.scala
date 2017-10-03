import scala.util.Random

object StrassenSerial {

  def strassenMultiply(a: Array[Array[Int]], b: Array[Array[Int]]): Array[Array[Int]] = {

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
    // set the matrix dimension
    val dim = 2

    val a = generateMatrix(dim)
    printMatrix(a)
    println()

    val b = generateMatrix(dim)
    printMatrix(b)
    println()

    // resultant matrix
    val c = strassenMultiply(a, b)
    printMatrix(c)
  }
}