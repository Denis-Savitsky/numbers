package ru.hes.app.proxy.core.dto

import derevo.circe.{decoder, encoder}
import derevo.derive
import ru.hes.app.domain.RawNum

@derive(encoder, decoder)
case class AnalysisRequest(number: RawNum)
