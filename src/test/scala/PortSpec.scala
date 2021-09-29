import zhttp.http.*
import zhttp.test.*
import zio.test.Assertion.*
import zio.test.*
import zio.test.environment.TestSystem

object PortSpec extends DefaultRunnableSpec:
  val test1 = testM("port defaults to 8080"):
    assertM(Port.get)(equalTo(8080.toChar))

  val test2 = testM("ports must be ints"):
    checkM(Gen.anyString):
      s =>
        for
          _ <- TestSystem.putEnv("PORT", s)
          port <- Port.get.run
        yield
          assert(port)(fails(isSubtype[Port.InvalidPortValue](anything)))

  val test3 = testM("ports must be in the valid char range"):
    checkM(Gen.anyInt):
      i =>
        for
          _ <- TestSystem.putEnv("PORT", i.toString)
          port <- Port.get.run
        yield
          assert(port):
            if (i < Char.MinValue || i > Char.MaxValue)
              fails(isSubtype[Port.InvalidPortValue](anything))
            else
              succeeds(equalTo(i.toChar))

  def spec = suite("port")(test1, test2, test3)