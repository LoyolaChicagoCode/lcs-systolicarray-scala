package edu.luc.etl.sigcse13.scala.lcs

import org.junit.Test
import org.junit.Assert._
import Fixtures._

/**
 * Simple JUnit-based tests.
 */
class Tests {

  @Test def testSum() {
    val root = SystolicArray(3, 3, f1)
    root.start()
    root.put(1)
    assertEquals(13, root.take())
  }

  @Test def testSample() {
    assertEquals(53, lcs(c0, c1))
  }
}
