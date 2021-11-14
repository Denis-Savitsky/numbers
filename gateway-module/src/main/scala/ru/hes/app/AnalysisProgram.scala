package ru.hes.app

import ru.hes.app.domain.{Prediction, RawNum}
import ru.hes.app.proxy.core.AnalysisProxyService
import ru.hes.app.proxy.kmeans.KMeansProxyService
import zio.{Has, Task, URLayer, ZIO}

class AnalysisProgram(analysisProxyService: AnalysisProxyService[Task], kmeansProxyService: KMeansProxyService[Task]) {
  def getNumbersForPrinting(n: Int): Task[List[RawNum]] =
    for {
      numbers <- analysisProxyService.getN(n)
    } yield numbers

  def analyzeNumbers(extraNum: Int): Task[List[Prediction]] =
    for {
      analyzedNumbers <- analysisProxyService.analyzeNum(RawNum(extraNum))
      predictions <- kmeansProxyService.getPredictions(analyzedNumbers)
    } yield predictions
}

object AnalysisProgram {
  val live: URLayer[Has[AnalysisProxyService[Task]] with Has[KMeansProxyService[Task]], Has[AnalysisProgram]] =
    (new AnalysisProgram(_, _)).toLayer

  def getNumbersForPrinting(n: Int): ZIO[Has[AnalysisProgram], Throwable, List[RawNum]] =
    ZIO.serviceWith[AnalysisProgram](_.getNumbersForPrinting(n))

  def analyzeNumbers(extraNum: Int): ZIO[Has[AnalysisProgram], Throwable, List[Prediction]] =
    ZIO.serviceWith[AnalysisProgram](_.analyzeNumbers(extraNum))
}