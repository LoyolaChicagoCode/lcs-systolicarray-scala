package edu.luc.etl.sigcse13.scala.lcs

import SystolicArray.Acc

/**
 * Main method with timing.
 */
object Main extends {
  import SystolicArray._

  def main(args: Array[String]) {
    try {
      require { 2 <= args.length && args.length <= 3 }
      val s0 = args(0)
      val s1 = args(1)
      val n  = if (args.length == 3) args(2).toInt else 1
      timedRun(s0, s1, n, lcs.f(s0, s1) _)
    } catch {
      case _: NumberFormatException => usage()
      case _: IllegalArgumentException => usage()
    }
  }

  def usage() { Console.err.println("usage: string0 string1 [ numberOfRuns ]") }

  def timedRun(s0: String, s1: String, n: Int, f: Acc[Int]) {
    val time0 = System.currentTimeMillis
    val root = SystolicArray(s0.length + 1, s1.length + 1, f)
    var result = -1
    root.start()
    (1 to n) foreach { _ =>
      root.put(1)
      result = root.take
    }
    val time1 = System.currentTimeMillis - time0
    root.stop()
    println("lcs(" + s0 + ", " + s1 + ") = " + result)
    println("total time = " + time1)
  }
}