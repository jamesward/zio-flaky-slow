import zhttp.http.Response.HttpResponse
import zhttp.http.{HttpData, Status}
import zio.*
import zio.duration.*
import zio.clock.Clock

/*
// Helper method to do effect hedging.
extension [Env, Err, R](effect: ZIO[Env, Err, R])
  /**
   * Hedges a retry of this effect against a predetermined latency-goal.
   */
  def hedge(duration: Duration = 1.seconds): ZIO[Env & Clock, Err, R] =
    // latency instead of always waiting N seconds.
    val delayedEffect =
      for
        _ <- ZIO.sleep(duration)
        result <- effect
      yield result
    effect.race(delayedEffect)
*/

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