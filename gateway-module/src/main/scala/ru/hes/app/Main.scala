package ru.hes.app

import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import ru.hes.app.http.Routes.routes
import ru.hes.app.proxy.core.AnalysisProxyService
import ru.hes.app.proxy.kmeans.KMeansProxyService
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.magic._
import zio.{App, ExitCode, Has, RIO, URIO, ZEnv, ZIO, ZLayer}

object Main extends App {

  val serve =
    ZIO.runtime[ZEnv with Has[AnalysisProgram]].flatMap { implicit runtime =>
      BlazeServerBuilder[RIO[Has[AnalysisProgram] with Clock with Blocking, *]](runtime.platform.executor.asEC)
        .bindHttp(8081, "localhost")
        .withHttpApp(Router("/" -> routes).orNotFound)
        .serve
        .compile
        .drain
    }

  val services = ZLayer.wire[Has[AnalysisProgram]](
    KMeansProxyService.live,
    AnalysisProxyService.live,
    AnalysisProgram.live,
  )

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = serve.provideCustomLayer(services).forever.exitCode
}
