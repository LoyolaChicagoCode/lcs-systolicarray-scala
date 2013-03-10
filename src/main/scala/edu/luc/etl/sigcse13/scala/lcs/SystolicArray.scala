package edu.luc.etl.sigcse13.scala.lcs


// An abstraction of a systolic array.

// begin-trait-SystolicArray
trait SystolicArray[T] {
  def start(): Unit
  def put(v: T): Unit
  def take(): T
  def stop(): Unit
}
// end-trait-SystolicArray

// The companion object for the systolic array abstracion.

// begin-object-SystolicArray
object SystolicArray {
  import scala.actors.Actor
  import scala.actors.Actor._
  import scala.concurrent.SyncVar
  import scala.language.implicitConversions

  private type LazyArray[T] = Stream[Stream[Cell[T]]]

  type Pos = (Int, Int)

  type Acc[T] = (Pos, Map[Pos, T]) => T

  case object Stop

  // Mini-logger structurally compatible with slf4j.
  // begin-object-logger
  private object logger {
    private val DEBUG = false
    // use call-by-name to ensure the argument is evaluated on demand only
    def debug(msg: => String) { if (DEBUG) println("debug: " + msg) }
    // add other log levels as needed
  }
  // end-object-logger

  // Factory method
  // begin-object-apply
  def apply[T](rows: Int, cols: Int, f: Acc[T]): SystolicArray[T] = {
    require { 0 < rows }
    require { 0 < cols }
    val result = new SyncVar[T]
    lazy val a: LazyArray[T] = Stream.tabulate(rows, cols) {
      (i, j) => new Cell(i, j, rows, cols, a, f, result)
    }
    val root = a(0)(0)
    new SystolicArray[T] {
      override def start() = root.start()
      override def put(v: T) { root ! ((-1, -1) -> v) }
      override def take() = result.take()
      override def stop() { root ! Stop }
    }
  }
  // end-object-apply


  // Internal cell implementation based on Scala actors.
  // begin-class-Cell
  protected class Cell[T](row: Int, col: Int, rows: Int, cols: Int, a: => LazyArray[T],
      f: Acc[T], result: SyncVar[T]) extends Actor { self =>

    require { 0 <= row && row < rows }
    require { 0 <= col && col < cols }

    logger.debug("creating (" + row + ", " + col + ")")

    override def act() {
      logger.debug("starting (" + row + ", " + col + ")")
      var start = true
      loop {
        logger.debug("waiting  (" + row + ", " + col + ")")
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
        react {
          case Stop => stopNeighbors() ; exit()
          case (p: Pos, v: T) => barrier1(n - 1)(f)(ms + (p -> v))
        }
      // one-way message: anything after react is skipped!
    }

    protected def applyToNeighbors(f: Cell[T] => Unit) {
      if (row < rows - 1)                   f(a(row + 1)(col    ))
      if (col < cols - 1)                   f(a(row    )(col + 1))
      if (row < rows - 1 && col < cols - 1) f(a(row + 1)(col + 1))
    }

    protected def startNeighbors() { applyToNeighbors { _.start() } }

    protected def propagate(ms: Map[Pos, T]) {
      val r = f((row, col), ms)
      val m = (row, col) -> r
      logger.debug("firing   " + m)
      if (row < rows - 1)                     a(row + 1)(col    ) ! m
      if (col < cols - 1)                     a(row    )(col + 1) ! m
      if (row < rows - 1 && col < cols - 1)   a(row + 1)(col + 1) ! m
      if (row >= rows - 1 && col >= cols - 1) result.put(r)
    }

    protected def stopNeighbors() { applyToNeighbors { _ ! Stop } }
  }
  // end-class-Cell

  // Conversion for adding navigation methods to Map.
  // begin-mapToHelper
  implicit class Helper[T](ms: Map[Pos, T]) {
    def north    (implicit current: (Pos, T)): T = ms.get((current._1._1 - 1, current._1._2    )).getOrElse(current._2)
    def west     (implicit current: (Pos, T)): T = ms.get((current._1._1    , current._1._2 - 1)).getOrElse(current._2)
    def northwest(implicit current: (Pos, T)): T = ms.get((current._1._1 - 1, current._1._2 - 1)).getOrElse(current._2)
  }
  // end-mapToHelper

  // Conversion for adding navigation methods to Pos.
  // begin-posToHelper
  implicit class PosHelper(p: Pos) {
    def north = p._1 - 1
    def west = p._2 - 1
    def isOnEdge = p._1 == 0 || p._2 == 0
  }
  // end-posToHelper
}
// end-object-SystolicArray