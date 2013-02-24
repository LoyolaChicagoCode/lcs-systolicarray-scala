import sbt._
import Keys._

object Build extends sbt.Build {
  import Dependencies._

  lazy val myProject = Project("lcs-systolicarray-scala-eclipse", file("."))
    .settings(
      organization  := "edu.luc.etl",
      version       := "0.0.1",
      scalaVersion  := V.scalaVersion,
      scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
      resolvers     ++= Dependencies.resolutionRepos,
      libraryDependencies ++=
        compile(scalaActors, slf4j) ++
        test(specs2, junit, mockito) ++
        runtime(logback)
    )

}