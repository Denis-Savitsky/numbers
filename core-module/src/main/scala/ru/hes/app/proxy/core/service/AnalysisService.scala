package ru.hes.app.proxy.core.service

import ru.hes.app.proxy.core.model.AnalysisResult
import zio.{Has, Task, ULayer, ZIO, ZLayer}

trait AnalysisService[F[_]] {
  def analyze(numbers: List[Int], number: Int): F[List[AnalysisResult]]
}

class AnalysisServiceImpl extends AnalysisService[Task] {
  override def analyze(numbers: List[Int], number: Int): Task[List[AnalysisResult]] =
    ZIO.foreachPar(numbers)(compared =>
      for {
        p <- place(number, compared)
        np <- numberWithPlace(number, compared)
      } yield AnalysisResult(p, np - p)
    )


  private def place(target: Int, compared: Int): Task[Int] = Task.succeed {
    val targetChars = target.toString.toList
    val comparedChars = compared.toString.toList
    targetChars.zip(comparedChars).count {
      case (a, b) => a == b
    }
  }

  private def numberWithPlace(target: Int, compared: Int): Task[Int] = Task.succeed {
    val targetChars = target.toString.groupMapReduce(identity)(_ => 1)(_ + _)
    val comparedChars = compared.toString.groupMapReduce(identity)(_ => 1)(_ + _)
    targetChars.map {
      case (s, n) => Math.min(n, comparedChars.getOrElse(s, 0))
    }.sum
  }
}

object AnalysisServiceImpl {
  val live: ULayer[Has[AnalysisService[Task]]] =
    ZLayer.succeed(new AnalysisServiceImpl)
}