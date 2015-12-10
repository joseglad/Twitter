package controllers;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is used to check if a tweet is already or not in the file
 */

public class DuplicationChecker {
	private BufferedReader reader;
	private String line;
	private String fileName;
	
	/**
	 * Constructor for a Duplication Checker
	 * @param fileName the file associated we want to read
	 */
	public DuplicationChecker(String fileName) {
		this.reader = null;
		this.line = null;
		this.fileName = fileName;
		
		try {
			this.reader = new BufferedReader(new FileReader(fileName));
		} catch(FileNotFoundException exc) {
			System.out.println("Erreur d'ouverture");
		}
	}
	
	/**
	 * function which return true if the new tweet TweetID is contained in the file of the DuplicationChecker 
	 * @param tweetID the tweet Id
	 * @return true if the tweet Id is contained in the string false else
	 * @throws IOException
	 */
	public Boolean isDuplication(String tweetID) throws IOException {
		try { 
			this.reader = new BufferedReader(new FileReader(this.fileName));
		} catch(FileNotFoundException exc) {
			System.out.println("Erreur d'ouverture");
		}
		
		while((this.line = this.reader.readLine()) != null) { //We give to the atribute line the the tweet in the file of the duplication Checker 
			if(this.line.startsWith(tweetID))
				return true;
		}
		return false;
	}
	
	/**
	 * Boolean function which return true it the tweet is a Retweet
	 * @param tweet one tweet
	 * @return true if the tweet start by RT, false else
	 */
	public Boolean isRT(String tweet){
		return tweet.startsWith("RT");
	}
  	
}