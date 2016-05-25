name := "wagon-challenge"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

mainClass in (Compile, run) := Some("challenge.Solution2")
