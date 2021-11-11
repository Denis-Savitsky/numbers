package ru.hes.app.gateway.api

import ru.hes.app.domain.{AnalyzedNum, RawNum}

trait AnalysisService[F[_]] {
  def getN(n: Int): F[List[RawNum]]
  def analyzeNum(extraNum: RawNum): F[List[AnalyzedNum]]
}

