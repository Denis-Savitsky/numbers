package ru.hes.app

import ru.hes.app.domain.{AnalyzedNum, RawNum}
import ru.hes.app.gateway.api.AnalysisProxyService
import zio.{Has, Task, URLayer, ZIO}

class AnalysisProgram(analysisProxyService: AnalysisProxyService[Task]) {
  def getNumbersForPrinting(n: Int): Task[List[RawNum]] =
    for {
      numbers <- analysisProxyService.getN(n)
    } yield numbers

  def analyzeNumbers(extraNum: Int): Task[List[AnalyzedNum]] =
    for {
      analyzedNumbers <- analysisProxyService.analyzeNum(RawNum(extraNum))
    } yield analyzedNumbers
}

object AnalysisProgram {
  val live: URLayer[Has[AnalysisProxyService[Task]], Has[AnalysisProgram]] =
    (new AnalysisProgram(_)).toLayer

  def getNumbersForPrinting(n: Int): ZIO[Has[AnalysisProgram], Throwable, List[RawNum]] =
    ZIO.serviceWith[AnalysisProgram](_.getNumbersForPrinting(n))

  def analyzeNumbers(extraNum: Int): ZIO[Has[AnalysisProgram], Throwable, List[AnalyzedNum]] =
    ZIO.serviceWith[AnalysisProgram](_.analyzeNumbers(extraNum))
}