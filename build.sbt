lazy val HelloAkkaHttp = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

name := "HelloAkkaHttp"

libraryDependencies ++= List(
  Library.scalaCheck % "test",
  Library.scalaTest  % "test"
)

initialCommands := """|import de.heikoseeberger.helloakkahttp._""".stripMargin
