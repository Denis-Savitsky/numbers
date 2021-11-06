package ru.hes.app

import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.decoding.DerivedDecoder.deriveDecoder
import ru.hes.app.analysis.model.AnalysisResult
import ru.hes.app.numberService.NumberService
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir._
import zio.{RIO, _}
import zio.blocking.Blocking
import zio.clock.Clock
//import zio.interop.catz.asyncInstance
import org.http4s.HttpRoutes

package object api {

  @derive(encoder, decoder)
  case class Fail(detailMessage: String) extends Throwable(detailMessage)

  val regenerate: ZServerEndpoint[Has[NumberService[Task]], Unit, Throwable, Unit, Any] =
    endpoint
      .post
      .in("regenerate")
      .errorOut(
        oneOf[Throwable](
          oneOfMappingFromMatchType(StatusCode.InternalServerError, jsonBody[Fail].description("error"))
        )
      )
      .zServerLogic(_ => NumberService.regenerate())

  val analyze: ZServerEndpoint[Has[NumberService[Task]], AnalyzeRequest, Throwable, List[AnalysisResult], Any] =
    endpoint
      .post
      .in("analyze")
      .in(jsonBody[AnalyzeRequest])
      .errorOut(
        oneOf[Throwable](
          oneOfMappingFromMatchType(StatusCode.InternalServerError, jsonBody[Fail].description("error"))
        )
      )
      .out(jsonBody[List[AnalysisResult]])
      .zServerLogic(r => NumberService.analyze(r.number, r.amount))

  val yaml = OpenAPIDocsInterpreter()
    .serverEndpointsToOpenAPI(List(regenerate, analyze), "main API", "0.1").toYaml

  val swaggerRoutes =
    ZHttp4sServerInterpreter().from(SwaggerUI[RIO[Has[NumberService[Task]] with Clock with Blocking, *]](yaml)).toRoutes

  val routes: HttpRoutes[ZIO[Has[NumberService[Task]] with Has[Clock.Service] with Has[Blocking.Service], Throwable, *]] =
    ZHttp4sServerInterpreter().from(
    List(regenerate, analyze)
  ).toRoutes

//  val allRoutes = routes <+> swaggerRoutes
}
