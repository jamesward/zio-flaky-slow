import zhttp.http.*
import zhttp.test.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.Assertion.Render.*
import zio.{Chunk, durationInt}

import java.io.IOException
import java.util.concurrent.TimeUnit

object SlowSpec extends ZIOSpecDefault:

  def spec = suite("slow"):
    test("should have a delay"):
      for
        _ <- TestRandom.feedInts(1000)
        req = Request()
        fork <- Slow.app(req).fork
        _ <- TestClock.adjust(1.minute)
        resp <- fork.join
      yield
        val expected = Response.text("hello, slow.  We took: 1000 millisconds.  Hope you got a coffee.")
        assert(resp)(isSubtype[Response](equalTo(expected)))
