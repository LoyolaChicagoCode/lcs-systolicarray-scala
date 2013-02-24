package edu.luc.etl.sigcse13.scala.lcs

// TODO unit tests
// TODO logging instead of printing
// TODO need sbt with these added dependencies

object Main extends App {
  import SystolicArray._

  val f1 = (p: Pos, ms: Map[Pos, Int]) => ms.values.sum

  val f2 = (p: Pos, ms: Map[Pos, Int]) => {
    implicit val currentPosAndDefaultValue = (p, 0)
    ms.north + ms.northwest + ms.west
  }

  val c0 = "Now is the time for all great women to come to the aid of their country"
  val c1 = "Now all great women will come to the aid of their country"

  val f3 = (p: Pos, ms: Map[Pos, Int]) => {
    implicit val currentPosAndDefaultValue = (p, 0)

    /* Java code for what to do with myself
    if (c0[i - 1] == c1[j - 1]))
       a[i][j] = a[i - 1][j - 1] + 1;
    else
       a[i][j] = Math.max(a[i - 1][j], a[i][j - 1]);
    *
    */

    if (p.isOnEdge)
      0
    else if (c0(p.north) == c1(p.west))
      ms.northwest + 1
    else
      math.max(ms.west, ms.north)
  }

  println("hello")
  val root = SystolicArray(c0.length + 1, c1.length + 1, f3)
  root.start()
  root.put(1)
  println("end result = " + root.take)
}
