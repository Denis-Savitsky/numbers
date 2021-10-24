package ru.hes.app.http

import cats.syntax.all._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.server.Router
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.syntax.kleisli._
import ru.hes.app.domain.{Num, RawNum}
import ru.hes.app.http.Routes.getNumbersForPrintingEndpoint
import sttp.capabilities.Effect
import sttp.tapir.Endpoint
import sttp.tapir.json.circe.{jsonBody, _}
import sttp.tapir.generic.auto._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir.{endpoint, path, stringBody, _}
import zio.clock.Clock
import zio.blocking.Blocking
import zio.interop.catz._
import zio.{App, ExitCode, IO, RIO, UIO, URIO, ZEnv, ZIO}

object Routes {
  val getNumbersForPrintingEndpoint: Endpoint[Int, String, List[RawNum], Any] =
    endpoint.get.in("getNumbers" / path[Int]("howMuch")).errorOut(stringBody).out(jsonBody[List[RawNum]])
  val persistenceRoutes: HttpRoutes[RIO[Clock with Blocking, *]] = ZHttp4sServerInterpreter()
    .from(getNumbersForPrintingEndpoint) { howMuch =>
      if (howMuch == 42) {
        UIO(List(RawNum(42)))
      } else {
        IO.fail("Иди подумой еще раз")
      }
    }
    .toRoutes

  val yaml: String = {
    import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
    import sttp.tapir.openapi.circe.yaml._
    OpenAPIDocsInterpreter().toOpenAPI(getNumbersForPrintingEndpoint, "Numbers docs", "1.0").toYaml
  }

  val swaggerRoutes: HttpRoutes[RIO[Clock with Blocking, *]] =
    ZHttp4sServerInterpreter().from(SwaggerUI[RIO[Clock with Blocking, *]](yaml)).toRoutes
}
