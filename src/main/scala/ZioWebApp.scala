import zhttp.http.*
import zhttp.http.Response.HttpResponse
import zhttp.service.{ChannelFactory, Client, EventLoopGroup, Server}
import zio.system.env
import zio.stream.*
import zio.*

import java.io.IOException

object ZioWebApp extends App:

  def upper[R, E >: Throwable](resp: HttpResponse[R, E]): ZIO[R, E, HttpResponse[R, E]] =
    if resp.status == Status.OK then
      val body = resp.content match
        case completeData: HttpData.CompleteData =>
          HttpData.CompleteData(Chunk.fromArray(String(completeData.data.toArray).toUpperCase.getBytes))
        case streamData: HttpData.StreamData[_, _] =>
          HttpData.Empty // todo
        case HttpData.Empty =>
          HttpData.Empty

      ZIO.succeed(resp.copy(content = body))
    else
      ZIO.fail(IllegalStateException())

  def flaky(url: String) =
    Client.request(url).flatMap(upper).retry(Schedule.recurs(5))

  def slow(url: String) =
    Client.request(url)

  def app(flakyUrl: String, slowUrl: String) = Http.collectM[Request]:
    case Method.GET -> Root / "race" =>
      slow(slowUrl).race(flaky(flakyUrl))
    //case Method.GET -> Root =>
    //  slow(slowUrl).hedge

  override def run(args: List[String]) =
    val clientEnv = ChannelFactory.auto ++ EventLoopGroup.auto()

    val server = for
      maybePort <- env("PORT")
      port = maybePort.map(_.toInt).getOrElse(8080)

      maybeFlakyUrl <- env("FLAKY_URL")
      flakyUrl = maybeFlakyUrl.getOrElse("http://localhost:8081/")

      maybeSlowUrl <- env("SLOW_URL")
      slowUrl = maybeSlowUrl.getOrElse("http://localhost:8082/")

      started <- Server.start(port, app(flakyUrl, slowUrl))
    yield started

    server.exitCode.provideCustomLayer(clientEnv)
