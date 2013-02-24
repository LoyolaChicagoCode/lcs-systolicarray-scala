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

  // "bare-metal" version of f3, does not run significantly faster
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

  def timedRun(l: String, n: Int, f: Acc[Int]) {
    print("using " + l)
    val time0 = System.currentTimeMillis
    val root = SystolicArray(c0.length + 1, c1.length + 1, f)
    root.start()
    (1 to n) foreach { _ =>
      root.put(1)
      root.take
    }
    println(": total time = " + (System.currentTimeMillis - time0))
  }

  (1 to 10) foreach { _ =>
    timedRun("f4", 100, f4)
    timedRun("f3", 100, f3)
  }

  System.exit(0)
}
