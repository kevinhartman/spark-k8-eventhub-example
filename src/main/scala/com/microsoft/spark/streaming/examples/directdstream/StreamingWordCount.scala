package com.microsoft.spark.streaming.examples.directdstream

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.eventhubs.EventHubsUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * an example application of Streaming WordCount
 */
object StreamingWordCount {

  def main(args: Array[String]): Unit = {

    if (args.length != 6) {
      println("Usage: program progressDir PolicyName PolicyKey EventHubNamespace EventHubName" +
        " BatchDuration(seconds)")
      sys.exit(1)
    }

    val progressDir = args(0)
    val policyName = args(1)
    val policykey = args(2)
    val eventHubNamespace = args(3)
    val eventHubName = args(4)
    val batchDuration = args(5).toInt

    val eventhubParameters = Map[String, String] (
      "eventhubs.policyname" -> policyName,
      "eventhubs.policykey" -> policykey,
      "eventhubs.namespace" -> eventHubNamespace,
      "eventhubs.name" -> eventHubName,
      "eventhubs.partition.count" -> "2",
      "eventhubs.consumergroup" -> "$Default"
    )

    val ssc = new StreamingContext(new SparkContext(), Seconds(batchDuration))

    val inputDirectStream = EventHubsUtils.createDirectStreams(
      ssc,
      eventHubNamespace,
      progressDir,
      Map(eventHubName -> eventhubParameters))

    inputDirectStream.foreachRDD { rdd =>
      rdd.flatMap(eventData => new String(eventData.getBody).split(" ").map(_.replaceAll(
        "[^A-Za-z0-9 ]", ""))).map(word => (word, 1)).reduceByKey(_ + _).collect().toList.
        foreach(println)
    }

    /* For TCP stream POC */
//    val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
//    val ssc = new StreamingContext(conf, Seconds(1))
//
//    val lines = ssc.socketTextStream("localhost", 9999)
//    val words = lines.flatMap(_.split(" "))
//
//    val pairs = words.map(word => (word, 1))
//    val wordCounts = pairs.reduceByKey(_ + _)
//    wordCounts.print()

    /* end */

    ssc.start()
    ssc.awaitTermination()
  }
}
