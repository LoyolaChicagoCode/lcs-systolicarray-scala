# Overview

An implementation of least common subsequence (LCS) based on a systolic array
implemented as Scala actors.

# Prerequisites

## Required

- Java Development Kit (JDK) through your package management system or from [Oracle](http://www.oracle.com/technetwork/java/javase/downloads)
- [sbt](http://www.scala-sbt.org/)

These really are the only required prerequisites.

## Optional

- [Eclipse 4.2.x IDE for Java Developers](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junosr1)
- [Scala IDE Eclipse plugin](http://scala-ide.org/download/milestone.html#scala_ide_21_milestone_3) corresponding to your Eclipse version

# Command-line

## Running the sample application

    $ sbt 'test:run string0 string1 [ numberOfRuns ]'

## Running the tests

    $ sbt test:test

or simply

    $ sbt test

# Eclipse

## Generating the configuration files

    $ sbt eclipse

## Running the sample application

- navigate to test > edu.luc.etl.sigcse13.scala.lcs > Main.scala
- right-click Main.scala > Run Configurations
- click on the Arguments tab and enter the two strings and optionally the
  number of times to run

## Running the tests

- right-click tests > Run As > Scala JUnit tests

See also near the end of [this document](http://scala-ide.org/docs/user/testingframeworks.html).
