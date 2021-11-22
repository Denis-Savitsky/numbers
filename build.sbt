import sbt.Keys.libraryDependencies
import sbt._

lazy val core = (project in file("core-module"))
  .settings(
    Settings.common
  )
  .settings(
    libraryDependencies ++= Dependencies.core,
    libraryDependencies ++= Dependencies.quill,
    libraryDependencies ++= Dependencies.derevo,
    libraryDependencies ++= Dependencies.config,
    )
  .settings(
    libraryDependencies ++= Dependencies.`zio-test`,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
  .settings(
    Compile / run / mainClass := Some("ru.hes.app.Main")
  )

lazy val gateway = (project in file("gateway-module"))
  .settings(
    Settings.common
  )
  .settings(
    libraryDependencies ++= Dependencies.core,
    libraryDependencies ++= Dependencies.derevo,
    libraryDependencies ++= Dependencies.httpClient
  )
  .settings(
    Compile / run / mainClass := Some("ru.hes.app.Main")
  )
