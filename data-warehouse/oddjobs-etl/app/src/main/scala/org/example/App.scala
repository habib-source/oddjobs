package org.example

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object App {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession (replaces SparkConf and SparkContext)
    val spark = SparkSession.builder()
      .appName("wiki_test")
      .master("spark://spark:7077")
      .getOrCreate()

    import spark.implicits._

    // Sample data as a Seq
    val lines = Seq("a b c", "a b")

    // Create a DataFrame from the sequence
    val df = lines.toDF("line")

    // Compute word frequencies using DataFrame transformations and functions
    val wordFreqDF = df
      .select(explode(split($"line", " ")).as("word"))
      .groupBy("word")
      .count()

    // Sort descending by frequency and take the top 10
    val topWords = wordFreqDF
      .orderBy($"count".desc)
      .limit(10)
      .as[(String, Long)]
      .collect()

    // Print the results
    println(s"================\nTop words (word, count): ${topWords.mkString(", ")}")

    // Convert DataFrame to a Map for collectAsMap equivalent
    val wordFreqMap = wordFreqDF
      .as[(String, Long)]
      .collect()
      .toMap
    println(s"Word frequency as map: $wordFreqMap")

    // Stop the Spark session cleanly
    spark.stop()
  }
}
