package edu.luc.etl.sigcse13.scala.lcs

trait SystolicArray[T] {
  def start(): Unit
  def put(v: T): Unit
  def take: T
}

object SystolicArray {
  import scala.actors.Actor
  import scala.actors.Actor._
  import scala.concurrent.SyncVar

  private type LazyArray[T] = Stream[Stream[Cell[T]]]

  type Pos = (Int, Int)

  type Acc[T] = (Pos, Map[Pos, T]) => T

  private val DEBUG = false

  private def log(s: => String) { if (DEBUG) println(s) }

  def apply[T](rows: Int, cols: Int, f: Acc[T]): SystolicArray[T] = {
    require { 0 < rows }
    require { 0 < cols }
    val result = new SyncVar[T]
    lazy val a: LazyArray[T] = Stream.tabulate(rows, cols) {
      (i, j) => new Cell(i, j, rows, cols, a, f, result)
    }
    val root = a(0)(0)
    new SystolicArray[T] {
      override def start() { root.start() }
      override def put(v: T) { root ! ((-1, -1) -> v) }
      override def take = result.take
    }
  }

  protected class Cell[T](row: Int, col: Int, rows: Int, cols: Int, a: => LazyArray[T],
      f: Acc[T], result: SyncVar[T]) extends Actor {

    require { 0 <= row && row < rows }
    require { 0 <= col && col < cols }

    log("creating (" + row + ", " + col + ")")

    override def act() {
      log("starting (" + row + ", " + col + ")")
      var start = true
      loop {
        log("waiting  (" + row + ", " + col + ")")
        barrier(if (row == 0 || col == 0) 1 else 3) { ms =>
          if (start) { startNeighbors() ; start = false }
          propagate(ms)
        }
        // one-way message: anything below here is skipped!
      }
    }

    protected def barrier(n: Int)(f: Map[Pos, T] => Unit): Unit =
      barrier1(n)(f)(Map.empty)

    protected def barrier1(n: Int)(f: Map[Pos, T] => Unit)(ms: Map[Pos, T]): Unit = {
      if (n <= 0)
        f(ms)
      else
        react { case (p: Pos, v: T) => barrier1(n - 1)(f)(ms + (p -> v)) }
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
      log("firing   " + m)
      if (row < rows - 1)                     a(row + 1)(col    ) ! m
      if (col < cols - 1)                     a(row    )(col + 1) ! m
      if (row < rows - 1 && col < cols - 1)   a(row + 1)(col + 1) ! m
      if (row >= rows - 1 && col >= cols - 1) result.put(r)
    }
  }

  implicit def mapToHelper[T](ms: Map[Pos, T]): Helper[T] = new Helper(ms)

  class Helper[T](ms: Map[Pos, T]) {
    def north    (implicit current: (Pos, T)): T = ms.get((current._1._1 - 1, current._1._2    )).getOrElse(current._2)
    def west     (implicit current: (Pos, T)): T = ms.get((current._1._1    , current._1._2 - 1)).getOrElse(current._2)
    def northwest(implicit current: (Pos, T)): T = ms.get((current._1._1 - 1, current._1._2 - 1)).getOrElse(current._2)
  }

  implicit def posToHelper(p: Pos): PosHelper = new PosHelper(p)

  class PosHelper(p: Pos) {
    def north = p._1 - 1
    def west = p._2 - 1
    def isOnEdge = p._1 == 0 || p._2 == 0
  }
}