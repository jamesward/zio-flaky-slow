import zhttp.http.{HttpData, Response, Status}
import zio.ZIO

def upper[R, E >: Throwable](resp: Response): ZIO[R, E, Response] =
  if resp.status == Status.Ok then
    resp.bodyAsString.map { content =>
      Response.text(content.toUpperCase)
    }
  else
    ZIO.fail(IllegalStateException())