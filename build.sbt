import sbt.Keys.libraryDependencies
import sbt._

lazy val db = (project in file("BDModule"))
  .settings(
    Settings.common
  )
  .settings(
    libraryDependencies ++= Dependencies.core
    )

lazy val gateway = (project in file("gateway"))
  .settings(
    Settings.common
  )
  .settings(
    libraryDependencies ++= Dependencies.core
  )
