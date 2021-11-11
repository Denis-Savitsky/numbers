package ru.hes.app.http

import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.generic.auto._
import org.http4s._
import ru.hes.app.AnalysisProgram
import ru.hes.app.domain.{AnalyzedNum, RawNum}
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir.{ZServerEndpoint, endpoint, oneOf, oneOfMappingFromMatchType, path, _}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.{Has, RIO, ZIO}

object Routes {
  @derive(encoder, decoder)
  case class Fail(detailMessage: String) extends Throwable(detailMessage)

  val getNumbersForPrinting: ZServerEndpoint[Has[AnalysisProgram], Int, Throwable, List[RawNum], Any] =
    endpoint
      .get
      .in("getNumbers" / path[Int]("howMuch"))
      .errorOut(
        oneOf[Throwable](
          oneOfMappingFromMatchType(StatusCode.InternalServerError, jsonBody[Fail].description("error"))
        )
      )
      .out(jsonBody[List[RawNum]])
      .zServerLogic(howMuch => AnalysisProgram.getNumbersForPrinting(howMuch))

  val analyzeNumbers: ZServerEndpoint[Has[AnalysisProgram], Int, Throwable, List[AnalyzedNum], Any] =
    endpoint
      .get
      .in("analyzeNumbers" / path[Int]("extraNumber"))
      .errorOut(
        oneOf[Throwable](
          oneOfMappingFromMatchType(StatusCode.InternalServerError, jsonBody[Fail].description("error"))
        )
      )
      .out(jsonBody[List[AnalyzedNum]])
      .zServerLogic(extraNumber => AnalysisProgram.analyzeNumbers(extraNumber))

  val yaml = OpenAPIDocsInterpreter()
    .serverEndpointsToOpenAPI(List(getNumbersForPrinting, analyzeNumbers), "gateway API", "0.1").toYaml

  val swaggerRoutes =
    ZHttp4sServerInterpreter().from(SwaggerUI[RIO[Has[AnalysisProgram] with Clock with Blocking, *]](yaml)).toRoutes

  val routes: HttpRoutes[ZIO[Has[AnalysisProgram] with Has[Clock.Service] with Has[Blocking.Service], Throwable, *]] =
    ZHttp4sServerInterpreter().from(
      List(getNumbersForPrinting, analyzeNumbers)
    ).toRoutes
}

