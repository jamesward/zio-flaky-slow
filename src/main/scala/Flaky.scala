import zhttp.http.*
import zhttp.service.Server
import zio.Console.printLine
import zio.{Random, ZIO, ZIOAppDefault}

import java.io.IOException

object Flaky extends ZIOAppDefault:

  val app = Http.collectZIO[Request]:
    case Method.GET -> !! =>
      for
        succeed <- Random.nextBoolean
        _ <- if succeed then printLine("not flaky") else printLine("flaky")
        result <-
          if succeed then
            ZIO.succeed(Response.text("hello, flaky"))
          else
            ZIO.fail(IOException("erggg"))
      yield result

  def run =
    Server.start(8081, app).exitCode
