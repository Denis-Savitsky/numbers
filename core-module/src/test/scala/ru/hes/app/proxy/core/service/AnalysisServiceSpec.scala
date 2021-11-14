package ru.hes.app.proxy.core.service

import ru.hes.app.proxy.core.model.AnalysisResult
import zio._
import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zio.test.{DefaultRunnableSpec, ZSpec}

object AnalysisServiceSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[TestEnvironment, Any] =
    suite("AnalysisServiceSpec")(
      testM("test case 136879 and 931771") {
        for {
          service <- ZIO.service[AnalysisService[Task]]
          result <- service.analyze(List(931771), 136879)
        } yield assert(result.head)(
          hasField("place", (r: AnalysisResult) => r.place, equalTo(2)) &&
          hasField("number", (r: AnalysisResult) => r.number, equalTo(2))
        )
      },
      testM("test case 931771 and 931771") {
        for {
          service <- ZIO.service[AnalysisService[Task]]
          result <- service.analyze(List(931771), 931771)
        } yield assert(result.head)(
          hasField("place", (r: AnalysisResult) => r.place, equalTo(6)) &&
            hasField("number", (r: AnalysisResult) => r.number, equalTo(0))
        )
      },
      testM("test case 773881 and 931771") {
        for {
          service <- ZIO.service[AnalysisService[Task]]
          result <- service.analyze(List(931771), 773881)
        } yield assert(result.head)(
          hasField("place", (r: AnalysisResult) => r.place, equalTo(1)) &&
            hasField("number", (r: AnalysisResult) => r.number, equalTo(3))
        )
      }
    ).provideCustomLayer(AnalysisServiceImpl.live)
}

