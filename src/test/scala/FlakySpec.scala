import zhttp.http.{Request, Response}
import zhttp.test.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.Assertion.Render.*

import java.io.IOException

object FlakySpec extends ZIOSpecDefault:

  def spec = suite("flaky")(
    test("true should succeed") {
      for
        _ <- TestRandom.feedBooleans(true)
        resp <- Flaky.app(Request())
      yield
        val expected = Response.text("hello, flaky")
        assert(resp)(isSubtype[Response](equalTo(expected)))
    },

    test("false should fail") {
      for
        _ <- TestRandom.feedBooleans(false)
        resp <- Flaky.app(Request()).exit
      yield
        assert(resp)(fails(isSome[IOException](anything)))
    },
  )
