import zhttp.http.*
import zhttp.service.{ChannelFactory, Client, EventLoopGroup, Server}
import zio.{durationInt, Schedule, ZIO, ZIOAppDefault}

object WebApp extends ZIOAppDefault:

  val app = Http.collectZIO[Request]:
    case Method.GET -> !! / "flaky" =>
      val url = "http://localhost:8081/"
      Client.request(url).flatMap(upper).retry(Schedule.recurs(5))

    case Method.GET -> !! / "slow" =>
      val url = "http://localhost:8082/"
      val req = Client.request(url).flatMap(_.getBodyAsString).map(Response.text(_))
      val hedge =
        for
          _ <- ZIO.sleep(1.second)
          result <- req
        yield result

      req.race(hedge)

  def run =
    val clientEnv = ChannelFactory.auto ++ EventLoopGroup.auto()
    Server.start(8080, app).exitCode.provideCustomLayer(clientEnv)
