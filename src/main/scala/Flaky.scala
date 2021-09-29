import zhttp.http.*
import zhttp.service.Server
import zio.random.nextBoolean
import zio.system.env
import zio.console.putStrLn
import zio.{App, ZIO}

import java.io.IOException

object Flaky extends App:

  val app = Http.collectM[Request]:
    case Method.GET -> Root =>
      for
        succeed <- nextBoolean
        _ <- if succeed then putStrLn("not flaky") else putStrLn("flaky")
        result <-
          if succeed then
            ZIO.succeed(Response.text("hello, flaky"))
          else
            ZIO.fail(IOException("erggg"))
      yield result

  override def run(args: List[String]) =
    val server = for
      maybePort <- env("PORT")
      port = maybePort.map(_.toInt).getOrElse(8081)
      started <- Server.start(port, app)
    yield started

    server.exitCode
