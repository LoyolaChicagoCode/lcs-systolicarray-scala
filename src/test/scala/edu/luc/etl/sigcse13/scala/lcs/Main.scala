package edu.luc.etl.sigcse13.scala.lcs

/**
 * Main method with timing.
 */
object Main extends App {
  import SystolicArray._
  import Fixtures._

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