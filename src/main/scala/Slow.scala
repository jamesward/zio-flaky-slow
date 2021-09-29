import zhttp.http.*
import zhttp.http.Response.HttpResponse
import zhttp.service.Server
import zio.{App, ZIO}
import zio.random.nextIntBetween
import zio.system.env
import zio.duration._
import zio.console.putStrLn

import java.io.IOException

object Slow extends App:

  val app = Http.collectM[Request]:
    case Method.GET -> Root =>
      for
        delay <- nextIntBetween(100, 5000)
        _ <- putStrLn(s"Delaying: $delay")
        _ <- ZIO.sleep(delay.millis)
      yield
        Response.text(s"hello, slow.  We took: $delay millisconds.  Hope you got a coffee.")

  override def run(args: List[String]) =
    val server = for
      maybePort <- env("PORT")
      port = maybePort.map(_.toInt).getOrElse(8082)
      started <- Server.start(port, app)
    yield started

    server.exitCode
