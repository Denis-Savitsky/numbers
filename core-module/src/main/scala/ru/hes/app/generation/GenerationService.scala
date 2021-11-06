package ru.hes.app.generation

import zio.{Has, Task, URLayer, ZIO}
import zio.random.Random

trait GenerationService[F[_]] {
  def generateList(): F[List[Int]]
}

class GenerationServiceImpl(random: Random.Service) extends GenerationService[Task] {
  override def generateList(): Task[List[Int]] =
    ZIO.foreachPar(List.fill(10000)(0)) { _ =>
      random.nextIntBetween(100000, 1000000)
    }
}

object GenerationServiceImpl {
  val live: URLayer[Has[Random.Service], Has[GenerationService[Task]]] =
    (new GenerationServiceImpl(_)).toLayer
}
