name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.1"

trapExit := false
scalafmtOnCompile := true

lazy val akkaVersion = "2.6.8"
val circeVersion = "0.12.3"
val AkkaVersion = "2.6.8"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "de.heikoseeberger" %% "akka-http-circe" % "1.31.0",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" ,
  "com.typesafe.akka" %% "akka-stream"
).map(_ % akkaVersion)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-testkit-typed",
  "com.typesafe.akka" %% "akka-stream-testkit"
).map(_ % akkaVersion % Test)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
