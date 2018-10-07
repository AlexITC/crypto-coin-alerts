name := "crypto-coin-alerts"
organization := "com.alexitc"
scalaVersion := "2.12.2"

fork in Test := true

scalacOptions ++= Seq(
//  "-Xfatal-warnings",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-target:jvm-1.8",
    "-encoding",
    "UTF-8",
    "-Xfuture",
    "-Xlint:missing-interpolator",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Ywarn-unused",
    "-Ywarn-unused-import"
)

scalafmtOnCompile in ThisBuild := true
scalafmtTestOnCompile in ThisBuild := true

resolvers += "jitpack" at "https://jitpack.io"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

routesImport += "com.alexitc.coinalerts.commons.PlayBinders._"

// don't include play generated classes into code coverage
coverageExcludedPackages := "<empty>;Reverse.*;router\\.*"

libraryDependencies ++= Seq(guice, evolutions, jdbc, ws, specs2 % Test)
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.3"

libraryDependencies += "com.alexitc" %% "playsonify" % "1.2.0"
libraryDependencies += "com.google.inject" % "guice" % "4.1.0"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.4"
libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212" // docker-it-scala has issues with 42.1.4

libraryDependencies += "commons-validator" % "commons-validator" % "1.6"
libraryDependencies += "de.svenkubiak" % "jBCrypt" % "0.4.1"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "ch.qos.logback" % "logback-core" % "1.2.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "com.github.bitsoex" % "bitso-java" % "v3.0.5"

libraryDependencies += "com.pauldijou" %% "jwt-core" % "0.14.1"

libraryDependencies ++= Seq(
    "com.beachape" %% "enumeratum" % "1.5.13"
)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
libraryDependencies += "com.alexitc" %% "playsonifytest" % "1.2.0" % Test

libraryDependencies ++= Seq(
    "com.spotify" % "docker-client" % "8.9.1",
    "com.whisk" %% "docker-testkit-scalatest" % "0.9.5" % "test",
    "com.whisk" %% "docker-testkit-impl-spotify" % "0.9.5" % "test"
)
