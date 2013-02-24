package edu.luc.etl.sigcse13.scala.lcs

object Fixtures {
  import SystolicArray._

  val c0 = "Now is the time for all great women to come to the aid of their country"

  val c1 = "Now all great women will come to the aid of their country"

  val f1 = (p: Pos, ms: Map[Pos, Int]) => ms.values.sum

  val f2 = (p: Pos, ms: Map[Pos, Int]) => {
    implicit val currentPosAndDefaultValue = (p, 0)
    ms.north + ms.northwest + ms.west
  }

  val f3 = lcs.f(c0, c1) _

  // "bare-metal" version of lcs, does not run significantly faster
  val f4 = (p: Pos, ms: Map[Pos, Int]) => {
    if (p._1 == 0 || p._2 == 0)
      0
    else if (c0(p._1 - 1) == c1(p._2 - 1))
      ms.get((p._1 - 1, p._2 - 1)).getOrElse(0) + 1
    else
      math.max(
        ms.get((p._1 - 1, p._2)).getOrElse(0),
        ms.get((p._1, p._2 - 1)).getOrElse(0))
  }
}