name := "zio-flaky"

scalaVersion := "3.1.1-RC1-bin-20210927-3f978b3-NIGHTLY"

val zioVersion = "2.0.0-RC1"

scalacOptions += "-language:experimental.fewerBraces"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % zioVersion,
  "dev.zio" %% "zio-test"     % zioVersion,

  "io.d11" %% "zhttp"         % "2.0.0-RC1",

  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,

  //"io.d11" %% "zhttp-test" % "1.0.0.0-RC17" % Test,
  // temporary snapshot
  "io.d11" %% "zhttp-test" % "2.0.0-RC1" % Test,
  //  "org.slf4j"  %  "slf4j-simple"        % "1.7.30",
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
