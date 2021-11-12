import sbt._
import Keys._

object Settings {

  val predef = Vector(
    "java.lang",
    "scala",
    "scala.Predef",
    "scala.util.chaining",
    "cats",
    "cats.syntax.all"
  ).mkString("-Yimports:", ",", "")

  val common = Seq(
    name := "Application",
    version := "0.1",
    scalaVersion := "2.13.6",
    scalacOptions := Seq(
      "-Ymacro-annotations",
      "-language:higherKinds,implicitConversions",
      "-Ywarn-unused:imports",
      "-Xlint:stars-align"
    ),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin(
      "org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full
    )
  )
}
