// Here's a link to a blog post outlining a similar build config:
// http://mkuthan.github.io/blog/2016/03/11/spark-application-assembly/

name := "spark-k8-eventhub-example"
version := "1.0"

scalaVersion := "2.11.8"
scalacOptions ++= Seq("-deprecation")

val sparkVersion = "2.1.0"

// Dependencies provided by the Spark distro (so we exclude them from our assembly)
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion
).map(_ % "provided")

// Bundled dependencies
libraryDependencies ++= Seq(
  "com.microsoft.azure" %% "spark-streaming-eventhubs" % "2.0.5"
)

// Configure fat JAR
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false) // Exclude Scala libs

