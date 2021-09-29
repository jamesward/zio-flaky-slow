import zhttp.test.*
import zhttp.http.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.Assertion.Render.*
import zio.test.environment.TestRandom

import java.io.IOException

object FlakySpec extends DefaultRunnableSpec:
  val test1 = testM("true should succeed"):
    for
      _ <- TestRandom.feedBooleans(true)
      req = Request(Method.GET -> URL(Path("/")))
      resp <- Flaky.app(req)
    yield
      val expected = Response.text("hello, flaky")
      assert(resp)(isSubtype[Response.HttpResponse[Any, Nothing]](equalTo(expected)))

  val test2 = testM("false should fail"):
    for
      _ <- TestRandom.feedBooleans(false)
      req = Request(Method.GET -> URL(Path("/")))
      resp <- Flaky.app(req).run
    yield
      assert(resp)(fails[Option[IOException]](anything))

  def spec = suite("flaky")(test1, test2)
