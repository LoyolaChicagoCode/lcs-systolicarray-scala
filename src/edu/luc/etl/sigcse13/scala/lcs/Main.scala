package edu.luc.etl.sigcse13.scala.lcs

import scala.actors.Actor
import scala.actors.Actor._

object Main extends App {
  println("hello")
  val root = SystolicArray(3, 5)
  root.start()
  (0 until 5) foreach { i =>
    Thread.sleep(3000)
    println("---round " + i + "----")
    root ! "lkj"
  }
}

object SystolicArray {
  def apply(n: Int, m: Int) = {
    require { 0 < n }
    require { 0 < m }
    lazy val a: Stream[Stream[Node]] = Stream.tabulate(n, m) {
      (i, j) => new Node(i, j, n, m, a)
    }
    a(0)(0)
  }
}

class Node(row: Int, col: Int, n: Int, m: Int, a: => Stream[Stream[Node]]) extends Actor {

  require { 0 <= row && row < n }
  require { 0 <= col && col < m }

  println("creating (" + row + ", " + col + ")")

  override def act() {
    println("starting (" + row + ", " + col + ")")
    var start = true
    loop {
      println("waiting  (" + row + ", " + col + ")")
      barrier(if (row == 0 || col == 0) 1 else 3) {
        () => { propagate(start) ; start = false }
      }
      // one-way message: anything below here is skipped!
    }
  }

  protected def barrier(n : Int)(p: () => Unit) {
    if (n <= 0)
      p()
    else
      react { case _ => barrier(n - 1)(p) }
    // one-way message: anything after react is skipped!
  }

  protected def propagate(start: Boolean) {
    if (start) {
      if (row < n - 1)                a(row + 1)(col    ).start()
      if (col < m - 1)                a(row    )(col + 1).start()
      if (row < n - 1 && col < m - 1) a(row + 1)(col + 1).start()
    }
    println("firing   (" + row + ", " + col + ")")
    if (row < n - 1)                a(row + 1)(col    ) ! "lkj"
    if (col < m - 1)                a(row    )(col + 1) ! "lkj"
    if (row < n - 1 && col < m - 1) a(row + 1)(col + 1) ! "lkj"
  }
}