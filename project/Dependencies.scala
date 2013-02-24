import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  object V {
    // version numbers common to multiple dependencies
    val scalaVersion = "2.10.1-RC1" // match Scala IDE 2.10 milestone
  }

  val scalaActors = "org.scala-lang"     % "scala-actors"     % V.scalaVersion
  val logback     = "ch.qos.logback"     %  "logback-classic" % "1.0.7"
  val slf4j       = "org.slf4j"          %  "slf4j-api"       % "1.7.2"
  val specs2      = "org.specs2"         %% "specs2"          % "1.14"
//  val scalaz      = "org.scalaz"         %% "scalaz-core"     % "6.0.4"
  val mockito     = "org.mockito"        %  "mockito-all"     % "1.9.5"
  val junit       = "junit"              %  "junit"           % "4.11"
}
