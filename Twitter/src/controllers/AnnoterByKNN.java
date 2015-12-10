package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.Tweet;

public class AnnoterByKNN {
	private int k;
	private KNNAnnotate knn;
	private BufferedReader reader;
	private ArrayList<Tweet> tweetList;
	private boolean debug = true;
	
	public AnnoterByKNN(int k) {
		if(this.debug) System.out.println("AnnoterByKNN : constructeur début");
		this.k = k;
		this.knn = new KNNAnnotate();
		this.tweetList = new ArrayList<Tweet>();
		this.initialiseTweetList("datas/newtweets.csv");
		if(this.debug) System.out.println("AnnoterByKNN : constructeur fin");
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
			notation = this.knn.knnAnalyse(tweet.getContenu(), "datas/tweetreference.csv", this.k);
			tweet.setNotation(notation+"");
		}
	}
	
	/**
	 * Retourne les tweets annotés
	 */
	public ArrayList<Tweet> getTweets(){
		
		this.annoteTweets();
		return this.tweetList;
		
	}

}
