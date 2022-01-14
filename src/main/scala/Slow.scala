import zhttp.http.*
import zhttp.service.Server
import zio.{Random, ZIOAppDefault, ZIO, durationInt}
import zio.Console.printLine

object Slow extends ZIOAppDefault:

  val app = Http.collectZIO[Request]:
    case Method.GET -> !! =>
      for
        delay <- Random.nextIntBetween(100, 5000)
        _ <- printLine(s"Delaying: $delay")
        _ <- ZIO.sleep(delay.millis)
      yield
        Response.text(s"hello, slow.  We took: $delay millisconds.  Hope you got a coffee.")

  def run =
    Server.start(8082, app).exitCode
