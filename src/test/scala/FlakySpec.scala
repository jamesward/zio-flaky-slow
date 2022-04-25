import zhttp.http.{Request, Response}
import zhttp.test.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.Assertion.Render.*

import java.io.IOException

object FlakySpec extends ZIOSpecDefault:
  val test1 = test("true should succeed"):
    for
      _ <- TestRandom.feedBooleans(true)
      req = Request()
      resp <- Flaky.app(req)
    yield
      val expected = Response.text("hello, flaky")
      assert(resp)(isSubtype[Response](equalTo(expected)))

  val test2 = test("false should fail"):
    for
      _ <- TestRandom.feedBooleans(false)
      req = Request()
      resp <- Flaky.app(req).exit
    yield
      assert(resp)(fails(isSome[IOException](anything)))

  def spec = suite("flaky")(test1, test2)
