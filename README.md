Trending hashtags in Twitter’s stream
=====================================================


### Enviroment versions required:

Spark: 2.2.1  
Python: 2.7  
Scala: 2.11

### Algorithm implementation approach:
Fetched top 5 trending hashtags from twitter stream by utilizing twitter’s streaming API based on Spark streaming. I used the fixed size Reservoir Sampling Algorithm for fetching the top 5 trending hashtags and calculating average length of tweets. I have maintained a fixed reservoir of given size. When the twitter stream is coming till the reservoir size number of tweets, directly store these tweets in the reservoir. After that, for the nth tweet, with probability (reservoir size / n) keep the nth tweet, else discard it. If I keep the nth tweet, it replaces one of the tweets in the reservoirx	 and need to randomly pick one to be replaced.  


### Library Dependencies: 

[tweepy(python)](http://docs.tweepy.org/en/v3.5.0/)  
[spark-streaming-twitter(Scala)](http://bahir.apache.org/docs/spark/current/spark-streaming-twitter/)  
_and “spark-streaming”._  


### Python command for getting Top-5 tweets using Reservoir Sampling Algorithm

* * *

Getting Top-5 tweets using _“Prasad\_Bhagwat\_ReservoirSampling.py”_ file

    spark-submit Prasad_Bhagwat_ReservoirSampling.py
    

Example usage of the above command is as follows:  

     ~/Desktop/spark-2.2.1/bin/spark-submit Prasad_Bhagwat_ReservoirSampling.py


### Scala command for getting Top-5 tweets using Reservoir Sampling Algorithm

* * *

Getting Top-5 tweets using _“Prasad\_Bhagwat\_ReservoirSampling.jar”_ file

    spark-submit --class ReservoirSampling Prasad_Bhagwat_ReservoirSampling.jar
    

Example usage of the above command is as follows:  

     ~/Desktop/spark-2.2.1/bin/spark-submit --class ReservoirSampling Prasad_Bhagwat_ReservoirSampling.jar

### Sample Output:

_Sample Output :_  
_The number of the tweets from beginning: 350_  
_Top 5 trending hashtags:_  
_USA : 3_  
_EXO : 2_  
_TheNewPainting : 1_  
_ButterflyCount : 1_  
_BarnOwl : 1_  
_The average length of the tweet is: 102.16_  
