package edu.luc.etl.sigcse13.scala.lcs

import org.specs2.mutable._
import org.specs2.matcher.Matcher
import org.specs2.matcher.ThrownExpectations
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import org.specs2.matcher.ThrownExpectations

@RunWith(classOf[JUnitRunner])
class Specs extends Specification {
  import Fixtures._

  "The systolic array" should {

    "return the expected sum result" in {
      val root = SystolicArray(3, 3, f1)
      root.start()
      root.put(1)
      root.take === 13
    }

    "return the expected LCS result for the sample strings" in {
      lcs(c0, c1) === 53
    }
  }
}