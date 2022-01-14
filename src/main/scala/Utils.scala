import zhttp.http.{HttpData, Response, Status}
import zhttp.service.Client.ClientResponse
import zio.ZIO

def upper[R, E >: Throwable](resp: ClientResponse): ZIO[R, E, Response] =
  if resp.status == Status.OK then
    resp.getBodyAsString.map:
      content => Response.text(content.toUpperCase)
  else
    ZIO.fail(IllegalStateException())