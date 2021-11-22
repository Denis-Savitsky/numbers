package ru.hes.app.proxy.core.impl

import io.circe.generic.auto._
import ru.hes.app.domain.{AnalyzedNumWithoutExtra, RawNum}
import ru.hes.app.proxy.core.dto.AnalysisRequest
import ru.hes.app.proxy.core.{AnalysisProxyService, dto}
import sttp.client3._
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.tapir._
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import zio.Task


class AnalysisProxyServiceImpl extends AnalysisProxyService[Task] {
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

  override def analyzeNum(extraNum: RawNum): Task[List[AnalyzedNumWithoutExtra]] = {
    val request = endpoint
      .get
      .in("analyze")
      .in(jsonBody[AnalysisRequest])
      .errorOut(stringBody)
      .out(jsonBody[List[AnalyzedNumWithoutExtra]])

    val analyzeRequest: Request[DecodeResult[Either[String, List[AnalyzedNumWithoutExtra]]], Any] =
      SttpClientInterpreter()
        .toRequest(request, Some(uri"http://localhost:8080"))
        .apply(dto.AnalysisRequest(extraNum))

    for {
      backend <- AsyncHttpClientZioBackend()
      response <- backend.send(analyzeRequest)
      numbers = response.body match {
        case DecodeResult.Value(Right(numbers)) => numbers.map(num => AnalyzedNumWithoutExtra(num.num, num.n, num.p))
        case _ => List.empty
      }
    } yield numbers
  }
}


