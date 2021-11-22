package ru.hes.app.proxy.core

import ru.hes.app.domain.{AnalyzedNumWithoutExtra, RawNum}
import ru.hes.app.proxy.core.impl.AnalysisProxyServiceImpl
import zio.{Has, Task, ULayer, ZLayer}

trait AnalysisProxyService[F[_]] {
  def getN(n: Int): F[List[RawNum]]
  def analyzeNum(extraNum: RawNum): F[List[AnalyzedNumWithoutExtra]]
}

object AnalysisProxyService {
  val live: ULayer[Has[AnalysisProxyService[Task]]] =
    ZLayer.succeed(new AnalysisProxyServiceImpl())
}