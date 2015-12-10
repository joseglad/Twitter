package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.Tweet;

public class AnnoterByBayesianFrequency {
	private BayesianFrequencyAnnotate bayesian;
	private BufferedReader reader;
	private ArrayList<Tweet> tweetList;
	private boolean debug = false;
	
	public AnnoterByBayesianFrequency(int n) {
		if(this.debug) System.out.println("AnnoterByBayesianFrequency : constructeur début");
		this.bayesian = new BayesianFrequencyAnnotate("datas/tweetreference.csv", n);
		this.tweetList = new ArrayList<Tweet>();
		this.initialiseTweetList("datas/newtweets.csv");
		if(this.debug) System.out.println("AnnoterByBayesianFrequency : constructeur fin");
	}
	
	private void initialiseTweetList(String tweetDatabase){
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
		if(this.debug) System.out.println("La liste de tweets a été correctement initialisé");
	}
	
	/**
	 * Annote tous les tweets recherchés
	 */
	private void annoteTweets(){
		Iterator<Tweet> it = this.tweetList.iterator();
		Tweet tweet;
		int notation;
		while(it.hasNext()) {
			tweet = it.next();
			notation = this.bayesian.getNotation(tweet);
			tweet.setNotation(notation+"");
		}
	}
	
	/**
	 * Retourne les tweets annotés par unigramme ou bigramme
	 */
	public ArrayList<Tweet> getTweets(){
		
		this.annoteTweets();
		return this.tweetList;
		
	}
	

	/**
	 * Annote tous les tweets recherchés
	 */
	private void annoteTweetsByUnibigramme(){
		Iterator<Tweet> it = this.tweetList.iterator();
		Tweet tweet;
		int notation;
		while(it.hasNext()) {
			tweet = it.next();
			notation = this.bayesian.annoteByUnibigramme(tweet);
			tweet.setNotation(notation+"");
		}
	}
	
	/**
	 * Retourne les tweets annotés par unibigramme
	 */
	public ArrayList<Tweet> getTweetsByUnibigramme(){
		
		this.annoteTweetsByUnibigramme();
		return this.tweetList;
		
	}

}

