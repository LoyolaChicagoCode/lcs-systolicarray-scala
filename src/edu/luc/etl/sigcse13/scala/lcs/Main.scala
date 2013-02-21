package edu.luc.etl.sigcse13.scala.lcs

import scala.actors.Actor
import scala.actors.Actor._
import scala.concurrent.SyncVar

object Main extends App {
  import SystolicArray.{Pos,mapToHelper}

  val f1 = (p: Pos, ms: Map[Pos, Int]) => ms.values.sum

  val f2 = (p: Pos, ms: Map[Pos, Int]) => {
    implicit val currentPosAndDefaultValue = (p, 0)
    ms.north + ms.northwest + ms.west
  }

  val c0 = "Now is the time for all great women to come to the aid of their country";
  val c1 = "Now all great women will come to the aid of their country";

  val f3 = (p: Pos, ms: Map[Pos, Int]) => {
    implicit val currentPosAndDefaultValue = (p, 0)

    /* Java code for what to do with myself
    if (c0[i - 1] == c1[j - 1]))
       a[i][j] = a[i - 1][j - 1] + 1;
    else
       a[i][j] = Math.max(a[i - 1][j], a[i][j - 1]);
    *
    */

    if (p._1 == 0 || p._2 == 0)
      0;
    else if (c0(p._1-1) == c1(p._2-1))
      ms.northwest + 1
    else
      math.max( ms.west, ms.north);
  }

  println("hello")
  val result = new SyncVar[Int]
  val root = SystolicArray(c0.length(), c1.length(), f3, result)
  root.start()
  root ! ((-1, -1) -> 1)
  println("end result = " + result.take)
}

object SystolicArray {

  type LazyArray[T] = Stream[Stream[Cell[T]]]

  type Pos = (Int, Int)

  type Acc[T] = (Pos, Map[Pos, T]) => T

  def apply[T](n: Int, m: Int, f: Acc[T], result: SyncVar[T]) = {
    require { 0 < n }
    require { 0 < m }
    lazy val a: LazyArray[T] = Stream.tabulate(n, m) {
      (i, j) => new Cell(i, j, n, m, a, f, result)
    }
    a(0)(0)
  }

  class Cell[T](row: Int, col: Int, rows: Int, cols: Int, a: => LazyArray[T],
      f: Acc[T], result: SyncVar[T]) extends Actor {

    require { 0 <= row && row < rows }
    require { 0 <= col && col < cols }

    println("creating (" + row + ", " + col + ")")

    override def act() {
      println("starting (" + row + ", " + col + ")")
      var start = true
      loop {
        println("waiting  (" + row + ", " + col + ")")
        barrier(if (row == 0 || col == 0) 1 else 3) { ms =>
          if (start) { startNeighbors() ; start = false }
          propagate(ms)
        }
        // one-way message: anything below here is skipped!
      }
    }

    protected def barrier(n : Int)(p: Map[Pos, T] => Unit): Unit =
      barrier1(n)(p)(Map.empty)

    protected def barrier1(n : Int)(p: Map[Pos, T] => Unit)(ms: Map[Pos, T]): Unit = {
      if (n <= 0)
        p(ms)
      else
        react { case m: (Pos, T) => barrier1(n - 1)(p)(ms + m) }
      // one-way message: anything after react is skipped!
    }

    protected def startNeighbors() {
      if (row < rows - 1)                   a(row + 1)(col    ).start()
      if (col < cols - 1)                   a(row    )(col + 1).start()
      if (row < rows - 1 && col < cols - 1) a(row + 1)(col + 1).start()
    }

    protected def propagate(ms: Map[Pos, T]) {
      val r = f((row, col), ms)
      val m = (row, col) -> r
      println("firing   " + m)
      if (row < rows - 1)                     a(row + 1)(col    ) ! m
      if (col < cols - 1)                     a(row    )(col + 1) ! m
      if (row < rows - 1 && col < cols - 1)   a(row + 1)(col + 1) ! m
      if (row >= rows - 1 && col >= cols - 1) result.set(r)
    }
  }

  implicit def mapToHelper[T](ms: Map[Pos, T]): Helper[T] = new Helper(ms)

  class Helper[T](ms: Map[Pos, T]) {
    def north    (implicit current: (Pos, T)): T = ms.get((current._1._1 - 1, current._1._2    )).getOrElse(current._2)
    def west     (implicit current: (Pos, T)): T = ms.get((current._1._1    , current._1._2 - 1)).getOrElse(current._2)
    def northwest(implicit current: (Pos, T)): T = ms.get((current._1._1 - 1, current._1._2 - 1)).getOrElse(current._2)
  }
}