package ru.hes.app.api

import derevo.circe.{decoder, encoder}
import derevo.derive

@derive(encoder, decoder)
case class AnalyzeRequest(number: Int, amount: Int)
