package ru.hes.app.numberService

import mouse.all.booleanSyntaxMouse
import ru.hes.app.db.service.NumberDao
import ru.hes.app.generation.GenerationService
import ru.hes.app.proxy.core.model.AnalysisResult
import ru.hes.app.proxy.core.service.AnalysisService
import zio._

trait NumberService[F[_]] {
  def regenerate(): F[Unit]

  def analyze(number: Int, n: Int): F[List[AnalysisResult]]
}

class NumberServiceImpl(
                         dao: NumberDao[Task],
                         generationService: GenerationService[Task],
                         analysisService: AnalysisService[Task]
                       ) extends NumberService[Task] {
  override def regenerate(): Task[Unit] = {
    for {
      list <- generationService.generateList()
      _ <- dao.replaceNumbers(list)
    } yield ()
  }

  private def extractNumbers(limit: Int): Task[List[Int]] =
    for {
      list <- dao.getNumbers(limit)
      nonEmptyList <- list.nonEmpty.fold(Task.succeed(list), generateNewList())
    } yield nonEmptyList


  private def generateNewList(): Task[List[Int]] = {
    for {
      list <- generationService.generateList()
      _ <- dao.insertNumbers(list).fork
    } yield list
  }

  override def analyze(number: Int, n: Int): Task[List[AnalysisResult]] = {
    for {
      list <- extractNumbers(n)
      result <- analysisService.analyze(list, n)
    } yield result

  }
}

object NumberServiceImpl {
  val live: URLayer[Has[NumberDao[Task]] with Has[GenerationService[Task]] with Has[AnalysisService[Task]], Has[NumberService[Task]]] =
    (new NumberServiceImpl(_, _, _)).toLayer
}

object NumberService {
  def regenerate(): ZIO[Has[NumberService[Task]], Throwable, Unit] =
    ZIO.serviceWith[NumberService[Task]](_.regenerate())

  def analyze(number: Int, n: Int): ZIO[Has[NumberService[Task]], Throwable, List[AnalysisResult]] =
    ZIO.serviceWith[NumberService[Task]](_.analyze(number, n))
}
