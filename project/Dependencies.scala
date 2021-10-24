import sbt._

object Dependencies {

  object V {
    val tapir = "0.19.0-M12"
    val zio = "1.0.12"
    val mouse = "1.0.6"
    val logback = "1.2.6"
  }

  val tapir = Seq("com.softwaremill.sttp.tapir" %% "tapir-zio" % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % V.tapir)
  val `tapir-circe` = Seq("com.softwaremill.sttp.tapir" %% "tapir-json-circe" % V.tapir)
  val `tapir-swagger` = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % V.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui" % V.tapir,
  )

  val zio = Seq(
    "dev.zio" %% "zio" % V.zio,
    "io.github.kitlangton" %% "zio-magic" % "0.3.8"
  )

  val mouse = Seq("org.typelevel" %% "mouse" % V.mouse)

  val logback = Seq(
    "ch.qos.logback" % "logback-classic" % V.logback % Runtime,
    "ch.qos.logback" % "logback-core" % V.logback % Runtime
  )

  val slf4j = Seq("org.slf4j" % "slf4j-api" % "1.7.32")

  val quill = Seq(
    "org.postgresql" % "postgresql" % "42.2.24",
    "io.getquill" %% "quill-jdbc-zio" % "3.10.0"
  )

  val derevo = Seq("tf.tofu" %% "derevo-circe" % "0.12.6")

  val core =
    zio ++
      tapir ++
      `tapir-circe` ++
      `tapir-swagger` ++
      mouse ++
      logback ++
      slf4j


}