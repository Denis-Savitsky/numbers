package ru.hes.app.db.service

import io.getquill.context.qzio.ImplicitSyntax.Implicit
import io.getquill.{PostgresZioJdbcContext, SnakeCase}
import io.getquill.context.ZioJdbc._
import mouse.all.booleanSyntaxMouse
import zio.IO

import java.sql.SQLException
//import io.getquill.context.qzio.ImplicitSyntax._
import ru.hes.app.db.model.NumberSchema
import ru.hes.app.db.model
import zio.{Has, Ref, Task, ZIO, ZLayer}

import java.io.Closeable
import javax.sql.DataSource

trait NumberDao[F[_]] {
  def replaceNumbers(newList: List[Int]): F[Unit]

  def getNumbers(limit: Int): F[List[Int]]

  def insertNumbers(list: List[Int]): F[List[Long]]
}

class NumberDaoImpl(
                     ds: DataSource with Closeable,
                     numberList: Ref[List[Int]]
                   ) extends NumberDao[Task] with NumberSchema {

  implicit val env = Implicit(Has(ds))

  override protected val ctx: PostgresZioJdbcContext[SnakeCase.type] = new PostgresZioJdbcContext(SnakeCase)

  import ctx._

  override def getNumbers(limit: Int): Task[List[Int]] = {
    for {
      listRef <- numberList.get
      list <- listRef.isEmpty.fold(getNumbersFromDb(limit), Task.succeed(listRef))
    } yield list
  }

  private def getNumbersFromDb(limit: Int): IO[SQLException, List[Int]] = {
    ctx.run(
      quote {
        (for {
          numbers <- numberSchema
        } yield numbers.number)
          .take(lift(limit))
      }
    ).implicitDS
  }

  private def deleteNumbers() = {
    ctx.run {
      (for {
        numbers <- numberSchema
      } yield numbers).delete
    }.implicitDS
  }

  override def insertNumbers(list: List[Int]): Task[List[Long]] = {
    ctx.run {
      quote {
        liftQuery(list.map(model.Number(0L, _))).foreach(e => numberSchema.insert(e))
      }
    }.implicitDS
  }

  override def replaceNumbers(newList: List[Index]): Task[Unit] =
    ctx.transaction {
      for {
        _ <- deleteNumbers()
        _ <- insertNumbers(newList)
      } yield ()
    }.implicitDS

}

object NumberDaoImpl {
  val live: ZLayer[Has[DataSource with Closeable], Nothing, Has[NumberDao[Task]]] =
    (for {
      ds <- ZIO.service[DataSource with Closeable]
      numbers <- Ref.make(List.empty[Int])
    } yield new NumberDaoImpl(ds, numbers)).toLayer
}
