import zhttp.http.*
import zhttp.test.*
import zio.random.nextIntBetween
import zio.clock.currentTime
import zio.duration.durationInt
import zio.Chunk
import zio.test.*
import zio.test.Assertion.*
import zio.test.Assertion.Render.*
import zio.test.environment.{TestRandom, TestClock}

import java.io.IOException
import java.util.concurrent.TimeUnit

object SlowSpec extends DefaultRunnableSpec:

  def spec = suite("slow"):
    testM("should have a delay"):
      for
        _ <- TestRandom.feedInts(1000)
        req = Request(Method.GET -> URL(Path("/")))
        fork <- Slow.app(req).fork
        _ <- TestClock.adjust(1.minute)
        resp <- fork.join
      yield
        //val chunks: Chunk[Byte] = resp.asInstanceOf[Response.HttpResponse[Any, Nothing]].content.asInstanceOf[HttpData.CompleteData].data
        //println(new String(chunks.toArray))
        val expected = Response.text("hello, slow.  We took: 1000 millisconds.  Hope you got a coffee.")
        assert(resp)(isSubtype[Response.HttpResponse[Any, Nothing]](equalTo(expected)))
