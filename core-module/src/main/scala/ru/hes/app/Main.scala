package ru.hes.app

import io.getquill.context.ZioJdbc.DataSourceLayer
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import ru.hes.app.analysis.service.AnalysisServiceImpl
import ru.hes.app.api.allRoutes
import ru.hes.app.db.service.NumberDaoImpl
import ru.hes.app.generation.GenerationServiceImpl
import ru.hes.app.numberService.{NumberService, NumberServiceImpl}
import ru.hes.app.proxy.core.service.AnalysisServiceImpl
import zio._
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz.asyncInstance
import zio.magic._

object Main extends zio.App {

  val serve: ZIO[zio.ZEnv with Has[NumberService[Task]], Throwable, Unit] = ZIO.runtime[ZEnv with Has[NumberService[Task]]].flatMap {
    implicit runtime =>
      BlazeServerBuilder[RIO[Has[NumberService[Task]] with Clock with Blocking, *]](runtime.platform.executor.asEC)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(Router("/" -> allRoutes).orNotFound)
        .serve
        .compile
        .drain
  }

  val services = ZLayer.wire[Has[NumberService[Task]]](
    DataSourceLayer.fromPrefix("application.db"),
    AnalysisServiceImpl.live,
    NumberDaoImpl.live,
    GenerationServiceImpl.live,
    NumberServiceImpl.live,
    ZEnv.live
  )

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    serve.provideCustomLayer(services).forever.exitCode
}
