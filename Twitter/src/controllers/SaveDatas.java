package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.Tweet;

/**
 * 
 *
 */
public class SaveDatas {
	private BufferedReader reader;
	private File destFile;
	private ArrayList<Tweet> tweetList;
	private DuplicationChecker checker;
	
	
	public SaveDatas() {
		this.tweetList = new ArrayList<Tweet>();
		this.initialiseTweetList("datas/newtweets.csv");
		this.destFile = new File("datas/tweetsdatabase.csv");
		
	}
	
	/**
	 * we save the correct tweets from the tweetList in the tweetsdatabase.csv 
	 */
	public void save() {
		this.checker = new DuplicationChecker("datas/tweetsdatabase.csv"); 
		FileWriter writer;
		Iterator<Tweet> it = this.tweetList.iterator();
		try {
			writer = new FileWriter(this.destFile, true);
			while(it.hasNext()) {
				Tweet tweet = it.next();
				//if the tweet is not a already in the tweetsdatabase.csv && the annotation of that tweet is equal neutral, negative or positive
				if((this.checker.isDuplication(tweet.getID()+"") == false) && ((tweet.getNotation().compareTo("0") == 0) || (tweet.getNotation().compareTo("2") == 0) || (tweet.getNotation().compareTo("4") == 0))) {
					writer.write(tweet.getString()+"\n");
				}
			}
			
			writer.close();
		}
		catch(IOException e) {
			
		}
	}
	
	/**
	 * Initialize the tweetList if she is empty by tweets in the tweetDatabase
	 * @param tweetDatabase
	 */
	public void initialiseTweetList(String tweetDatabase){
		String line = "";
		if(this.tweetList.iterator().hasNext() == false){
			
			try {
				this.reader = new BufferedReader(new FileReader(tweetDatabase));
			} catch(FileNotFoundException exc) {
				System.out.println("Erreur d'ouverture");
			}
			
			/* Ne fonctionne que si chaque tweet est sur une unique ligne */
			try {
				while((line = this.reader.readLine()) != null) {
					this.tweetList.add(new Tweet(line));
				}
			} catch(IOException e) {
				System.out.println("Erreur de lecture du ficher "+tweetDatabase);
			}
		}
	}
}