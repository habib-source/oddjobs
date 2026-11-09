package org.example
import org.apache.spark.{SparkConf, SparkContext}

object App {
  def main(args: Array[String]): Unit = {
    // Create a Spark configuration and context
    val conf = new SparkConf()
      .setAppName("wiki_test")
      .setMaster("spark://spark:7077")
      .set("spark.serializer", "org.apache.spark.serializer.JavaSerializer") // Add this line
    val sc = new SparkContext(conf)

    val lines = Array("a b c", "a b").toSeq

    // Compute word frequencies
    val wordFreq = sc.parallelize(lines)
      .flatMap(_.split(" "))
      .map((_, 1))
      .reduceByKey(_ + _)

    // Sort descending by frequency and take the top 10 using .take(10)
    val topWords = wordFreq.sortBy(s => s._2, ascending = false)
      .map(x => (x._2, x._1))
      .take(10)

    // Print the results
    println(s"================\nTop words (count, word): ${topWords.mkString(", ")}")
    println(s"Word frequency as map: ${wordFreq.collectAsMap().toString()}")

    // Stop the Spark context cleanly
    sc.stop()
  }
}
