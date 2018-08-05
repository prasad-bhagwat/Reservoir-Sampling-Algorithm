// Imports required for the program
import org.apache.spark.{SparkConf, SparkContext}
import twitter4j.Status
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.twitter._
import scala.collection.mutable.{HashMap, LinkedHashMap, ListBuffer}
import scala.util.Random
import scala.util.control.Breaks._


object ReservoirSampling {
  // Global variable declarations
  // API configuration parameters
  val user_key            = ""
  val user_secret         = ""
  val user_access_token   = ""
  val user_access_secret  = ""

  // Global variable declarations
  var tweet_count         = 0
  // Dictionary of the form {hashtag : No. of occurances}
  var hashtag_dict        = HashMap[String, Int]()
  // Dictionary of the form {tweet Id : length of tweet}
  var tweets_dict         = HashMap[Int, Int]()
  // Dictionary of the form {tweet Id : list of hashtags from tweet Id}
  var tweet_hashtags_dict = HashMap[Int, ListBuffer[String]]()
  // Reservoir size
  var reservoir_size      = 100

  // Reservoir Sampling Function
  def twitter_stream_listener(status: Status): Unit ={
    // Creating reservoir of reservoir size tweets
    if (tweet_count < reservoir_size)
    {
      // Retrieving current tweet's hashtags
      val current_hashtags    = status.getHashtagEntities.map(_.getText())
      val hashtag_list        = ListBuffer[String]()
      // Looping over each hashtag from current tweet's hashtags
      for (hashtag_index <- status.getHashtagEntities.map(_.getText()).indices)
      {
        // Appending current hashtag to list of hashtags present in current tweet
        hashtag_list.append(current_hashtags(hashtag_index))
        // Updating count of current hashtag occured in the hashtag list
        var hashtag_count = hashtag_dict.getOrElse(current_hashtags(hashtag_index), 0)
        hashtag_count += 1
        hashtag_dict += (current_hashtags(hashtag_index) -> hashtag_count)
      }
      // Updating hashtags present in tweet and corresponding length of tweet
      tweet_hashtags_dict += (tweet_count -> hashtag_list)
      tweets_dict         += (tweet_count -> status.getText.size)
    }
    else
    {
      // Generating probability S / N to be used for comparison
      val new_tweet_probability   = reservoir_size.toFloat / tweet_count.toFloat
      val comparing_probability   = Random.nextFloat()
      // If new tweet is going to replace randomly chosen tweet from the reservoir
      if (comparing_probability < new_tweet_probability)
      {
        // Retrieving current tweet's hashtags
        val current_hashtags    = status.getHashtagEntities.map(_.getText())
        val hashtag_list        = ListBuffer[String]()
        // Generating random number between 0 to reservoir size - 1
        val dict_index          = Random.nextInt(reservoir_size - 1)
        // Retrieving hashtags for the tweet getting replaced in reservoir
        val previous_hashtags   = tweet_hashtags_dict.getOrElse(dict_index, List())
        // Updating count of hashtags occured in the previous tweet's list
        if (previous_hashtags.size > 0)
        {
          for (hashtag <- previous_hashtags)
          {
            if (hashtag_dict.getOrElse(hashtag, 0) > 1)
            {
              var hashtag_count = hashtag_dict.getOrElse(hashtag, 0)
              hashtag_count -= 1
              hashtag_dict += (hashtag -> hashtag_count)
            }
            else
            {
              hashtag_dict -= hashtag
            }
          }
        }
        // Looping over each hashtag from current tweet's hashtags
        for (hashtag_index <- current_hashtags.indices)
        {
          // Appending current hashtag to list of hashtags present in current tweet
          hashtag_list.append(current_hashtags(hashtag_index))
          // Updating count of current hashtag occured in the hashtag list
          var hashtag_count = hashtag_dict.getOrElse(current_hashtags(hashtag_index), 0)
          hashtag_count += 1
          hashtag_dict += (current_hashtags(hashtag_index) -> hashtag_count)
        }
        // Updating hashtags present in tweet and corresponding length of tweet
        tweet_hashtags_dict += (tweet_count -> hashtag_list)
        tweets_dict         += (tweet_count -> status.getText.size)

        // Printing the information of reservoir's current state by showing 5 most trending tweets
        println("The number of the twitter from beginning: "+ tweet_count)
        println("Top 5 hot hashtags:")
        val hashtags    = LinkedHashMap(hashtag_dict.toSeq.sortWith(_._2 > _._2):_*)
        var counter     = 0
        for (hashtag <- hashtags){
          breakable {
            if (counter != 5) {
              println(hashtag._1 + " : " + hashtag._2)
              counter += 1
            }
            else {
              break
            }
          }
        }
        println("The average length of the twitter is: " + tweets_dict.foldLeft(0)(_+_._2).toFloat / tweets_dict.values.size.toFloat)
        println("\n"+"\n")
      }
    }
    // Incrementing tweet count
    tweet_count += 1
  }

  // Main Function
  def main(args: Array[String]): Unit = {
    // Setting the system properties to generate OAuth credentials for twitter4j
    System.setProperty("twitter4j.oauth.consumerKey", user_key)
    System.setProperty("twitter4j.oauth.consumerSecret", user_secret)
    System.setProperty("twitter4j.oauth.accessToken", user_access_token)
    System.setProperty("twitter4j.oauth.accessTokenSecret", user_access_secret)

    // Creating Streaming Context using spark configuration
    val spark_config    = new SparkConf().setAppName("ReservoirSampling").setMaster("local[2]")
    val stream_context  = new StreamingContext(spark_config, Seconds(1))
    val tweets_context  = stream_context.sparkContext.setLogLevel("ERROR")

    // Generating stream of tweets using TwitterUtils
    val stream          = TwitterUtils.createStream(stream_context, None, Array("#"))
    stream.foreachRDD(status => status.collect().foreach(x => twitter_stream_listener(x)))

    // Starting Twitter stream
    stream_context.start()
    stream_context.awaitTermination()
  }
}
