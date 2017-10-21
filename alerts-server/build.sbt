name := "crypto-coin-alerts"
organization := "com.alexitc"
scalaVersion := "2.12.2"

scalacOptions ++= Seq(
//  "-Xfatal-warnings",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-Xfuture",
  "-Xlint:missing-interpolator",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)


resolvers += "jitpack" at "https://jitpack.io"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala)

libraryDependencies ++= Seq(guice, evolutions, jdbc)

libraryDependencies += "com.google.inject" % "guice" % "4.1.0"
libraryDependencies += "com.alexitc" %% "play-request-tracer" % "0.1.0"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.github.bitsoex" % "bitso-java" % "v3.0.5"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
