package ru.hes.app.domain

import derevo.circe.{decoder, encoder}
import derevo.derive

sealed trait Num
@derive(encoder, decoder)
case class RawNum(value: Int) extends Num
@derive(encoder, decoder)
case class AnalyzedNum(extraNum: Int, num: Int, p: Int, n: Int) extends Num
@derive(encoder, decoder)
case class AnalyzedNumWithoutExtra(num: Int, p: Int, n: Int) extends Num
@derive(encoder, decoder)
case class Prediction(num: Int, p: Int, n: Int, cluster: Int) extends Num
