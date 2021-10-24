package ru.hes.app.db

import zio.UIO

trait Persistence {
}
trait Logging {
  def log(line: String): UIO[Unit]
}