package ru.hes.app.gateway.api.impl

import ru.hes.app.domain.{AnalyzedNum, AnalyzedNumRaw, RawNum}
import ru.hes.app.gateway.api
import ru.hes.app.gateway.api.AnalysisProxyService
import ru.hes.app.gateway.api.dto.AnalysisRequest
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.{Has, Task, ULayer, ZLayer}


class AnalysisProxyServiceImpl extends AnalysisProxyService[Task] {

  import io.circe.generic.auto._
  import sttp.client3._
  import sttp.tapir._
  import sttp.tapir.client.sttp.SttpClientInterpreter
  import sttp.tapir.generic.auto._
  import sttp.tapir.json.circe._

  override def getN(n: Int): Task[List[RawNum]] = {
    val request = endpoint
      .get
      .in("numbers" / path[Int]("howMuch"))
      .errorOut(stringBody)
      .out(jsonBody[List[RawNum]])

    val getNRequest: Request[DecodeResult[Either[String, List[RawNum]]], Any] =
      SttpClientInterpreter()
        .toRequest(request, Some(uri"http://localhost:8080"))
        .apply(n)

    for {
      backend <- AsyncHttpClientZioBackend()
      response <- backend.send(getNRequest)
      numbers = response.body match {
        case DecodeResult.Value(Right(numbers)) => numbers
        case _ => List.empty
      }
    } yield numbers
  }

  override def analyzeNum(extraNum: RawNum): Task[List[AnalyzedNum]] = {
    val request = endpoint
      .get
      .in("analyze")
      .in(jsonBody[AnalysisRequest])
      .errorOut(stringBody)
      .out(jsonBody[List[AnalyzedNumRaw]])

    val analyzeRequest: Request[DecodeResult[Either[String, List[AnalyzedNumRaw]]], Any] =
      SttpClientInterpreter()
        .toRequest(request, Some(uri"http://localhost:8080"))
        .apply(api.dto.AnalysisRequest(extraNum))

    for {
      backend <- AsyncHttpClientZioBackend()
      response <- backend.send(analyzeRequest)
      numbers = response.body match {
        case DecodeResult.Value(Right(numbers)) => numbers.map(num => AnalyzedNum(extraNum.value, num.num, num.n, num.p))
        case _ => List.empty
      }
    } yield numbers
  }
}

object AnalysisProxyServiceImpl {
  val live: ULayer[Has[AnalysisProxyService[Task]]] =
    ZLayer.succeed(new AnalysisProxyServiceImpl())
}
