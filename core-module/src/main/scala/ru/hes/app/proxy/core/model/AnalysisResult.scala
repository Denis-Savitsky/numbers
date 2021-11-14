package ru.hes.app.proxy.core.model

import derevo.circe.{decoder, encoder}
import derevo.derive

@derive(encoder, decoder)
case class AnalysisResult(place: Int, number: Int)
