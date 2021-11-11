package ru.hes.app.generation

import zio._
import zio.random.Random
import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zio.test.{DefaultRunnableSpec, ZSpec}

object GenerationServiceSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[TestEnvironment, Any] =
    suite("GenerationServiceSpec")(
      testM("generated list contains 10,000 numbers") {
        for {
          service <- ZIO.service[GenerationService[Task]]
          numbers <- service.generateList()
        } yield assert(numbers.size)(equalTo(10000))
      }.provideCustomLayer(Random.live >>> GenerationServiceImpl.live)
    )
}
