package ru.hes.app.proxy.kmeans.impl

import io.circe.generic.auto._
import ru.hes.app.domain.{AnalyzedNumWithoutExtra, Prediction}
import ru.hes.app.proxy.kmeans.KMeansProxyService
import ru.hes.app.proxy.kmeans.dto.PredictionRequest
import sttp.client3._
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.tapir.client.sttp.SttpClientInterpreter
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.{endpoint, stringBody, _}
import zio.Task


class KMeansProxyServiceImpl extends KMeansProxyService[Task] {
  override def getPredictions(numbers: List[AnalyzedNumWithoutExtra]): Task[List[Prediction]] = {
    val request = endpoint
      .get
      .in("getPredictions")
      .in(jsonBody[PredictionRequest])
      .errorOut(stringBody)
      .out(jsonBody[List[Prediction]])

    val getPredictionsRequest =
      SttpClientInterpreter()
        .toRequest(request, Some(uri"http://localhost:8082"))
        .apply(PredictionRequest(numbers))

    for {
      backend <- AsyncHttpClientZioBackend()
      response <- backend.send(getPredictionsRequest)
      numbers = response.body match {
        case DecodeResult.Value(Right(predictions)) => predictions
        case _ => List.empty
      }
    } yield numbers
  }
}

