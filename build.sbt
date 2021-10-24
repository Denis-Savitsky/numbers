import sbt.Keys.libraryDependencies
import sbt._

lazy val db = (project in file("BDModule"))
  .settings(
    Settings.common
  )
  .settings(
    libraryDependencies ++= Dependencies.core,
    libraryDependencies ++= Dependencies.quill,
    libraryDependencies ++= Dependencies.derevo
    )

lazy val gateway = (project in file("gateway"))
  .settings(
    Settings.common
  )
  .settings(
    libraryDependencies ++= Dependencies.core
  )
