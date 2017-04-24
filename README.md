# Spark Streaming and Event Hub on Kubernetes
A Spark Streaming example project / template for consuming data from EventHub.

## Overview
Event Hub integration with Spark Streaming [is hardly new](https://github.com/hdinsight/spark-eventhubs), but most commonly it's found within the context of HDInsight. With the rise of containerized applications, people are floking in droves to the versatile world of container-based clustering provided by Kubernetes. This project demonstrates the integration of Spark Streaming and Event Hub for use on a Kubernetes cluster using a vanilla deployment of [the official Spark Helm chart.](https://github.com/kubernetes/charts/tree/master/stable/spark)

## The Configuration
The interesting portion of this project is contained within its SBT project files. This is where we define how the application should packaged into its minimal form such that it can be deployed to and run by the vanilla Spark cluster.

The deployment of the Spark cluster on Kubernetes itself is a prerequisite to deploying the example application, and is not covered here. Take a look at the official Spark Helm chart linked above for details.

### Packaging
We'll package and distribute an "Uber" JAR to the Spark cluster in order to deploy our app. This JAR will include everything necessary to run our application on the cluster, and ideally nothing else. We're using the [SBT Assembly](https://github.com/sbt/sbt-assembly) plugin in order to generate our Uber JAR, which will attempt to package all libraries by default.

### Library Dependencies
Take a look at <i>build.sbt</i> at the root of this repo. Spark library components that are a part of the Spark distribution will of course already be present at runtime, so in this section, we mark them as <b>provided</b>. This tells the <i>assembly</i> task not to include the class files for these libraries in the JAR that it produces.
```scala
// Dependencies provided by the Spark distro (so we exclude them from our assembly)
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion
).map(_ % "provided")
```

In this section, we configure the <i>assembly</i> task not to include Scala's standard libraries (they will also be present at runtime). Our project targets a version of Scala that's compatible with the Spark deployment.
```scala
// Configure fat JAR
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false) // Exclude Scala libs
```

Client libraries for consuming data from services such as Event Hub are workload specific, and are therefore <b>not part of the Spark distribution</b>. As such, we <b><i>do not</i></b> mark our dependency on the Event Hub client library as "provided". This ensures its contents are included in the JAR produced by the <i>assembly</i> task.

```scala
// Bundled dependencies
libraryDependencies ++= Seq(
  "com.microsoft.azure" %% "spark-streaming-eventhubs" % "2.0.5"
)
```

## Deployment
TODO
