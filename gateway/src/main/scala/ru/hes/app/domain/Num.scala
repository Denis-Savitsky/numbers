package ru.hes.app.domain

sealed trait Num
case class RawNum(value: Int) extends Num
case class AnalyzedNum(value: Int, p: Int, n: Int) extends Num
