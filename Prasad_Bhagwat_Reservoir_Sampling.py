# Imports required for the program
import time
import random
from tweepy import StreamListener, OAuthHandler, Stream


# Global variable declarations
tweet_count         = 0
# Dictionary of the form {hashtag : No. of occurances}
hashtag_dict        = dict()
# Dictionary of the form {tweet Id : length of tweet}
tweets_dict         = dict()
# Dictionary of the form {tweet Id : list of hashtags from tweet Id}
tweet_hashtags_dict = dict()
# Reservoir size
reservoir_size      = 100


# API configuration parameters
user_key            = ""
user_secret         = ""
user_access_token   = ""
user_access_secret  = ""


# StreamListener class
class twitter_stream_listener(StreamListener):
    # Function is called when a new status arrives
    def on_status(self, status):
        global reservoir_size, tweet_count, hashtag_dict, tweets_dict, tweet_hashtags_dict
        # Creating reservoir of reservoir size tweets
        if tweet_count < reservoir_size:
            # Retrieving current tweet's hashtags
            current_hashtags    = status.entities.get('hashtags')
            hashtag_list        = list()
            # Looping over each hashtag from current tweet's hashtags
            for hashtag_index in range(0 , len(current_hashtags)):
                # Appending current hashtag to list of hashtags present in current tweet
                hashtag_list.append(current_hashtags[hashtag_index].get('text'))
                # Updating count of current hashtag occured in the hashtag list
                if current_hashtags[hashtag_index].get('text') in hashtag_dict:
                    hashtag_dict[current_hashtags[hashtag_index].get('text')] += 1
                else:
                    hashtag_dict[current_hashtags[hashtag_index].get('text')] = 1
            # Updating hashtags present in tweet and corresponding length of tweet
            tweet_hashtags_dict[tweet_count]    = hashtag_list
            tweets_dict[tweet_count]            = len(status.text)

        # When tweet count exceeds the Reservoir size
        else:
            # Generating probability S / N to be used for comparison
            new_tweet_probability   = float(reservoir_size) / float(tweet_count)
            comparing_probability   = random.random()
            # If new tweet is going to replace randomly chosen tweet from the reservoir
            if comparing_probability < new_tweet_probability:
                # Retrieving current tweet's hashtags
                current_hashtags    = status.entities.get('hashtags')
                hashtag_list        = list()
                # Generating random number between 0 to reservoir size - 1
                dict_index          = random.randint(0, reservoir_size - 1)
                # Retrieving hashtags for the tweet getting replaced in reservoir
                previous_hashtags   = tweet_hashtags_dict[dict_index]
                # Updating count of hashtags occured in the previous tweet's list
                if len(previous_hashtags) > 0:
                    for hashtag in previous_hashtags:
                        if hashtag_dict[hashtag] > 1:
                            hashtag_dict[hashtag] -= 1
                        else:
                            del hashtag_dict[hashtag]
                # Looping over each hashtag from current tweet's hashtags
                for hashtag_index in range(0, len(current_hashtags)):
                    # Appending current hashtag to list of hashtags present in current tweet
                    hashtag_list.append(current_hashtags[hashtag_index].get('text'))
                    # Updating count of current hashtag occured in the hashtag list
                    if current_hashtags[hashtag_index].get('text') in hashtag_dict:
                        hashtag_dict[current_hashtags[hashtag_index].get('text')] += 1
                    else:
                        hashtag_dict[current_hashtags[hashtag_index].get('text')] = 1
                # Updating hashtags present in tweet and corresponding length of tweet
                tweet_hashtags_dict[dict_index] = hashtag_list
                tweets_dict[dict_index]         = len(status.text)

                # Printing the information of reservoir's current state by showing 5 most trending tweets
                print "The number of the twitter from beginning:", tweet_count
                print "Top 5 hot hashtags:"
                hashtags    = sorted(hashtag_dict, key= hashtag_dict.get, reverse=True)
                counter     = 0
                for hashtag in hashtags:
                    if counter != 5:
                        print hashtag.encode("utf8"), ":", hashtag_dict[hashtag]
                        counter += 1
                    else:
                        break
                print "The average length of the twitter is:", float(sum(tweets_dict.values())) / len(tweets_dict.values())
                print "\n", "\n", "\n"
        # Incrementing tweet count
        tweet_count += 1
        return True

    # Function is called when a non-200 status code is returned
    def on_error(self, status_code):
        if status_code == 420:
            return False


# Main Function
def main():
    # Authorizing user key and user secret using OAuthHandler
    auth = OAuthHandler(user_key, user_secret)

    # Accessing the user's access key and access secret
    auth.set_access_token(user_access_token, user_access_secret)

    # Creating StreamListener class object
    stream_object   = twitter_stream_listener()
    
    # Getting stream of tweets using stream_object and authorization
    hashtag_stream  = Stream(auth, stream_object, tweet_mode='extended', timeout=20)
    hashtag_stream.sample(languages=["en"], async=True)
    

# Entry point of the program
if __name__ == '__main__':
    main()
