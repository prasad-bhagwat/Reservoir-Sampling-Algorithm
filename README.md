Reservoir Sampling Algorithm
=====================================================


### Enviroment versions required:

Spark: 2.2.1  
Python: 2.7  
Scala: 2.11

### Problem Description:
Using Twitter API of streaming to implement the fixed size Reservoir Sampling Algorithm and tracking the popular tags on tweets and calculating average length of tweets.  


### Python command for getting Top-5 tweets algorithm using Reservoir Sampling Algorithm

* * *

Computing edge betweenness using _“Prasad\_Bhagwat\_ReservoirSampling.py”_ file

    spark-submit Prasad_Bhagwat_ReservoirSampling.py
    

Example usage of the above command is as follows:  

     ~/Desktop/spark-2.2.1/bin/spark-submit Prasad_Bhagwat_ReservoirSampling.py


### Scala command for getting Top-5 tweets algorithm using Reservoir Sampling Algorithm

* * *

Computing edge betweenness using _“Prasad\_Bhagwat\_ReservoirSampling.jar”_ file

    spark-submit --class ReservoirSampling Prasad_Bhagwat_ReservoirSampling.jar
    

Example usage of the above command is as follows:  

     ~/Desktop/spark-2.2.1/bin/spark-submit --class ReservoirSampling Prasad_Bhagwat_ReservoirSampling.jar

### Sample Output:

_Sample Output :_  
_The number of the twitter from beginning: 350_  
_Top 5 hot hashtags:_  
_USA : 3_  
_EXO : 2_  
_TheNewPainting : 1_  
_ButterflyCount : 1_  
_BarnOwl : 1_  
_The average length of the twitter is: 102.16_  
