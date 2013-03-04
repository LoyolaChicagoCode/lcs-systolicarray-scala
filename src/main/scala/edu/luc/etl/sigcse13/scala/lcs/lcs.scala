package edu.luc.etl.sigcse13.scala.lcs

/**
 * Object containing the LCS implementation based on systolic arrays.
 */
// begin-object-lcs
object lcs {
  import SystolicArray._

  def f(c0: String, c1: String)(p: Pos, ms: Map[Pos, Int]) = {
    implicit val currentPosAndDefaultValue = (p, 0)
    if (p.isOnEdge)
      0
    else if (c0(p.north) == c1(p.west))
      ms.northwest + 1
    else
      math.max(ms.west, ms.north)
  }

  def apply(c0: String, c1: String): Int = {
    val root = SystolicArray(c0.length + 1, c1.length + 1, f(c0, c1))
    root.start()
    root.put(1)
    root.take
  }
}
// end-object-lcs