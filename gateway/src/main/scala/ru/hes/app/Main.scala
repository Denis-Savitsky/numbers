package ru.hes.app

import cats.syntax.all._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.syntax.kleisli._
import ru.hes.app.http.Routes.{persistenceRoutes, swaggerRoutes}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.{App, ExitCode, RIO, URIO, ZEnv, ZIO}

object Main extends App {

  val serve: ZIO[ZEnv, Throwable, Unit] =
    ZIO.runtime[ZEnv].flatMap { implicit runtime => // This is needed to derive cats-effect instances for that are needed by http4s
      BlazeServerBuilder[RIO[Clock with Blocking, *]](runtime.platform.executor.asEC)
        .bindHttp(8080, "localhost")
        .withHttpApp(Router("/" -> (persistenceRoutes <+> swaggerRoutes)).orNotFound)
        .serve
        .compile
        .drain
    }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = serve.exitCode
}
