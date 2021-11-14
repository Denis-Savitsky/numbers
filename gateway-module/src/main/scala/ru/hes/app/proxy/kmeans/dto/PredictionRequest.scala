package ru.hes.app.proxy.kmeans.dto

import derevo.circe.{encoder, decoder}
import derevo.derive
import ru.hes.app.domain.AnalyzedNumWithoutExtra

@derive(encoder, decoder)
case class PredictionRequest(numbers: List[AnalyzedNumWithoutExtra])
