package ru.hes.app

import ru.hes.app.domain.{AnalyzedNum, RawNum}
import ru.hes.app.gateway.api.AnalysisService
import zio.{Has, Task, URLayer, ZIO}

class AnalysisProgram(analysisService: AnalysisService[Task]) {
  def getNumbersForPrinting(n: Int): Task[List[RawNum]] =
    for {
      numbers <- analysisService.getN(n)
    } yield numbers

  def analyzeNumbers(extraNum: Int): Task[List[AnalyzedNum]] =
    for {
      analyzedNumbers <- analysisService.analyzeNum(RawNum(extraNum))
    } yield analyzedNumbers
}

object AnalysisProgram {
  val live: URLayer[Has[AnalysisService[Task]], Has[AnalysisProgram]] =
    (new AnalysisProgram(_)).toLayer

  def getNumbersForPrinting(n: Int): ZIO[Has[AnalysisProgram], Throwable, List[RawNum]] =
    ZIO.serviceWith[AnalysisProgram](_.getNumbersForPrinting(n))

  def analyzeNumbers(extraNum: Int): ZIO[Has[AnalysisProgram], Throwable, List[AnalyzedNum]] =
    ZIO.serviceWith[AnalysisProgram](_.analyzeNumbers(extraNum))
}