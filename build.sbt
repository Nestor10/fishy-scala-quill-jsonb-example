val scala3Version = "3.7.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "fishy-scala-quill-jsonb-example",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "io.getquill" %% "quill-jdbc-zio" % "4.8.3",
      "org.postgresql" % "postgresql" % "42.7.7",
      "dev.zio" %% "zio" % "2.1.19",
      "dev.zio" %% "zio-json" % "0.6.2"
    )
  )

Global / onChangedBuildSource := ReloadOnSourceChanges