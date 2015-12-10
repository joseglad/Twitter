package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.Tweet;

public class CounterByClassOfTweet {
	private BufferedReader reader;
	private String line;
	private ArrayList<Tweet> tweetList;
	private int neutralN, positiveN, negativeN, N;
	
	/**
	 * Construit un nouveau compteur par classe de tweets
	 * @param tweetDatabase fichier contenant les tweets
	 */
	public CounterByClassOfTweet(String tweetDatabase) {
		this.tweetList = new ArrayList<Tweet>();
		this.initialiseTweetList(tweetDatabase);
		Iterator<Tweet> itTweet = tweetList.iterator();
		
		while(itTweet.hasNext()) {
			this.N++;
			Tweet currentTweet = itTweet.next();
			if(currentTweet.getNotation().compareTo("0") == 0) {
				this.negativeN++;
			}
			else {
				if(currentTweet.getNotation().compareTo("2") == 0) {
					this.neutralN++;
				}
			
				else {
					if(currentTweet.getNotation().compareTo("4") == 0) {
						this.positiveN++;
					}
				}
			}
		}
	}
	
	/**
	 * Initialise la liste de tweets avec la base de tweets tweetDatabase
	 * @param tweetDatabase la base de tweets
	 */
	private void initialiseTweetList(String tweetDatabase){
		if(this.tweetList.iterator().hasNext() == false){
			
			try {
				this.reader = new BufferedReader(new FileReader(tweetDatabase));
			} catch(FileNotFoundException exc) {
				System.out.println("Erreur d'ouverture "+tweetDatabase);
			}
			
			/* Ne fonctionne que si chaque tweet est sur une unique ligne */
			try {
				while((this.line = this.reader.readLine()) != null) {
					this.tweetList.add(new Tweet(this.line));
				}
			} catch(IOException e) {
				System.out.println("Erreur de lecture du ficher "+tweetDatabase);
			}
		}
	}
	
	/**
	 * Retourne le nombre de tweets positifs
	 * @return le nombre de tweets positifs
	 */
	public int getPositiveTweetNumber(){
		return this.positiveN;
	}
	
	/**
	 * Retourne le nombre de tweets neutres
	 * @return le nombre de tweets neutres
	 */
	public int getNeutralTweetNumber(){
		return this.neutralN;
	}
	
	/**
	 * Retourne le nombre de tweets negatifs
	 * @return le nombre de tweets negatifs
	 */
	public int getNegativeTweetNumber(){
		return this.negativeN;
	}
	/**
	 * Retourne le nombre total de tweets 
	 * @return le nombre total de tweets
	 */
	public int getTweetNumber(){
		return this.N;
	}

}
