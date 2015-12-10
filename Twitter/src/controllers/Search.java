package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Tweet;


import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class Search {
	private ConfigurationBuilder cb;
	private TwitterFactory tf;
	private Twitter twitter;
	private QueryResult tweets;
	private List<Tweet> listOfTweets;
	private DuplicationChecker checker;
	private Cleaner cleaner;
	private boolean debug = true;
	
	public Search() {
		this.setConfigurationBuilder();
	}
	
	/**
	 * Initialise the configuration builder without proxy
	 */
	public void setConfigurationBuilder(){
		this.cb = new ConfigurationBuilder();
		this.cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("0BUF9UklVeSk7UHfhDtuc0j8h")
		  .setOAuthConsumerSecret("NEmWsZZDBgGhuml45Jq8XjNQxo7zqQrQEI9ULy7Llh4Bac49ny")
		  .setOAuthAccessToken("323113726-VlqrEMX7aAZVentlC8NPhl94RavpLIDYtwdQNigD")
		  .setOAuthAccessTokenSecret("j7CCVqisYAHGGH4mX495bEQlzYWYOV2UG7KYch483Qqf2");
		
		this.tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
	}
	
	/**
	 * Initialise the proxy
	 */
	public void setProxyConfiguration() {
		this.cb = new ConfigurationBuilder();
		this.cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("0BUF9UklVeSk7UHfhDtuc0j8h")
		  .setOAuthConsumerSecret("NEmWsZZDBgGhuml45Jq8XjNQxo7zqQrQEI9ULy7Llh4Bac49ny")
		  .setOAuthAccessToken("323113726-VlqrEMX7aAZVentlC8NPhl94RavpLIDYtwdQNigD")
		  .setOAuthAccessTokenSecret("j7CCVqisYAHGGH4mX495bEQlzYWYOV2UG7KYch483Qqf2");
		
		this.cb.setHttpProxyHost("cache-etu.univ-lille1.fr");
		this.cb.setHttpProxyPort(3128);
		
		this.tf = new TwitterFactory(cb.build());
		this.twitter = tf.getInstance();
	}
	
	
	/**
	 * Return the rate limit
	 * @return rate limit
	 */
	public int getRateLimit() {
		
		 try {
		        Map<String ,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
		        for (String endpoint : rateLimitStatus.keySet()) 
		        {
		            if (endpoint.equals("/application/rate_limit_status"))
		               return rateLimitStatus.get(endpoint).getRemaining();
		           //return rateLimitStatus.get(endpoint).getSecondsUntilReset(); //return when the tweet limit is reset
		        }
		    } catch (TwitterException te) 
		        {
		            te.printStackTrace();
		            System.out.println("Failed to get rate limit status: " + te.getMessage());
		            System.exit(-1);
		        }       
		    return -1; 
	}
	
	/**
	 * Search the word mot in Twitter
	 * @param mot word to search
	 * @throws TwitterException
	 */
	public void search(String mot) throws TwitterException{
		if(this.debug) System.out.println("Search search : début");
		Query query = new Query(mot);
		/* Sélection des tweets d'origine française */
		query.setLang("FR");
		query.setLocale("FR");
		this.checker = new DuplicationChecker("datas/tweetsdatabase.csv");
		this.cleaner = new Cleaner();
		this.tweets = this.twitter.search(query);
		if(this.debug) System.out.println("Search search : fin");
	}
	

	/**
	 * Return the tweets according to id, utilisateur, tweet, Date, Requete, Polarité
	 * @return tweets the tweets
	 * @throws IOException 
	 */
	public String getTweets(String requete) throws IOException {
		if(this.debug) System.out.println("Search getTweets : début");
		this.listOfTweets = new ArrayList<Tweet>();
		String theTweets = "";
		String tweetContent = "";
		for(Status tweet : this.tweets.getTweets()) {
			if((this.checker.isDuplication(tweet.getId()+"") == false) && (this.checker.isRT(tweet.getText()) == false)) {
				/* Here clean the tweet */
				tweetContent = this.cleaner.cleanTweet(tweet.getText());
				//tweetContent = tweet.getText();
				
				
				/* Add the tweet to others */
				theTweets += tweet.getId() + ";" + tweet.getUser().getScreenName() + ";" + tweetContent + ";" + tweet.getCreatedAt() + ";" + requete+ ";-1\n";
				this.listOfTweets.add(new Tweet(tweet.getId() + ";" + tweet.getUser().getScreenName() + ";" + tweetContent + ";" + tweet.getCreatedAt() + ";" + requete+ ";-1"));
			}
		}
		System.out.println(theTweets);
		return theTweets;
	}
	
	public List<Tweet> getListOfTweets() {
		return this.listOfTweets;
	}
}