package ru.hes.app.db.model

import io.getquill.{PostgresZioJdbcContext, SnakeCase}

case class Number(id: Long, number: Int)

trait NumberSchema {

  protected val ctx: PostgresZioJdbcContext[SnakeCase.type]

  import ctx._

  val numberSchema = quote {
    querySchema[Number]("app.numbers")
  }
}
