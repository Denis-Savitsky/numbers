package ru.hes.app.proxy.kmeans

import ru.hes.app.domain.{AnalyzedNumWithoutExtra, Prediction}
import ru.hes.app.proxy.kmeans.impl.KMeansProxyServiceImpl
import zio.{Has, Task, ULayer, ZLayer}

trait KMeansProxyService[F[_]] {
  def getPredictions(numbers: List[AnalyzedNumWithoutExtra]): F[List[Prediction]]
}

object KMeansProxyService {
  val live: ULayer[Has[KMeansProxyService[Task]]] =
    ZLayer.succeed(new KMeansProxyServiceImpl())
}