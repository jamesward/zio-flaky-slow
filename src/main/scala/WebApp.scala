import zhttp.http.*
import zhttp.http.Response.HttpResponse
import zhttp.service.{ChannelFactory, Client, EventLoopGroup, Server}
import zio.*
import zio.duration.durationInt
import zio.system.env
import zio.stream.*

import java.io.IOException

object WebApp extends App:

  val app = Http.collectM[Request]:
    case Method.GET -> Root / "flaky" =>
      val url = "http://localhost:8081/"
      Client.request(url).flatMap(upper).retry(Schedule.recurs(5))

    case Method.GET -> Root / "slow" =>
      val url = "http://localhost:8082/"
      val req = Client.request(url)
      val hedge =
        for
          _ <- ZIO.sleep(1.second)
          result <- req
        yield result

      req.race(hedge)

  override def run(args: List[String]) =
    val clientEnv = ChannelFactory.auto ++ EventLoopGroup.auto()
    Server.start(8080, app).exitCode.provideCustomLayer(clientEnv)
