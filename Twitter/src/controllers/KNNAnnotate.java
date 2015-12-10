package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.Tweet;

/**
 * class used to annotate the tweets with the KNN way
 *
 */
public class KNNAnnotate {
	private BufferedReader reader;
	private String line;
	private double wordTotal;
	private double similarWord;
	private ArrayList<Tweet> tweetList;
	private Tweet[] kNearNeighbour;
	private boolean debug = false;
	
	public KNNAnnotate(){
		this.reader = null;
		this.line = null;
		this.wordTotal = 0;
		this.similarWord = 0;
		this.tweetList = new ArrayList<Tweet>();
	}
	
	/**
	 * Calcule la distance entre deux tweets
	 * @param t1 the first word
	 * @param t2 the second word
	 * @return d la distance entre deux tweets
	 */
	public double wordDistance(String t1, String t2){
		t1.trim();
		t2.trim();
		String[] tab1 = t1.split(" ");
		String[] tab2 = t2.split(" ");
		this.wordTotal = tab1.length + tab2.length;
		//Pour 2 tweets vides
		if(this.wordTotal == 0) return 0;
		
		for(int i = 0; i < tab1.length; i++){
			for(int j = 0; j < tab2.length; j++){
				if(tab1[i].compareTo(tab2[j]) == 0){
					this.similarWord++;
				}
			}
		}
		
		return ((this.wordTotal -this.similarWord)/this.wordTotal);
	}
	
	public void initialiseTweetList(String tweetDatabase){
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
	 * 
	 * @param k
	 * @return
	 * PS : en cas d'égalité c'est la séquentialité qui l'emporte, premier rencontré, premier retourné
	 */
	public int vote(int k){
		/*
		 * notation[i] i=0..3
		 * i signification
		 * 0 non annoté
		 * 1 negatif
		 * 2 neutre
		 * 3 positif
		 */
		int notation[] = new int[4];
		int iOfMax = 0; 
		int i;
		
		for(i = 0; i < k; i++){
			if(this.kNearNeighbour[i].getNotation().compareTo("-1") == 0) notation[0]++;
			else if(this.kNearNeighbour[i].getNotation().compareTo("0") == 0) notation[1]++;
			else if(this.kNearNeighbour[i].getNotation().compareTo("2") == 0) notation[2]++;
			else if(this.kNearNeighbour[i].getNotation().compareTo("4") == 0) notation[3]++;
		}
		
		for(i = 0; i < notation.length; i++){
			if(this.debug) System.out.println("notation["+i+"] vaut "+notation[i]);
		}
		
		//séquentialité
		for(i = 1; i < notation.length; i++){
			if(notation[i] > notation[iOfMax])
				iOfMax = i;
		}
		
		switch(iOfMax){
		case 1:
			return 0;
		case 2:
			return 2;
		case 3:
			return 4;
		default:
			return -1;
		}
	}
	
	/**
	 * 
	 * @param tweet un tweet
	 * @param tweetDatabase une base de tweets annotés
	 * @param k le nombre de voisins
	 * @return la notation du tweet
	 */
	public int knnAnalyse(final String tweet, String tweetDatabase, int k){
		this.initialiseTweetList(tweetDatabase);
		this.kNearNeighbour = new Tweet[k];
		Iterator<Tweet> itTweetList = this.tweetList.iterator();
		Tweet currentTweet;
		int i = 0;
		int indexOfFurthest = 0;
	
		
		while(i < k && itTweetList.hasNext()){
			this.kNearNeighbour[i] = itTweetList.next();
			i++;
		}
		
		
		
		while(itTweetList.hasNext()){
			currentTweet = itTweetList.next();
			boolean tweetHasAdded = false;
			for(i = 0; i < k && !tweetHasAdded; i++){
				if(this.wordDistance(currentTweet.getContenu(), tweet) < this.wordDistance(this.kNearNeighbour[i].getContenu(), tweet)) {
					//On cherche l'indice du proche voisin ayant la distance la plus grande
					int j;
					for(j = 0; j < k; j++){
						if(this.wordDistance(this.kNearNeighbour[j].getContenu(), tweet) > this.wordDistance(this.kNearNeighbour[indexOfFurthest].getContenu(), tweet))
							indexOfFurthest = j;
					}
					//On a l'indice
					//On le supprime des proches voisins ET On met le tweet de tweetList dans proches voisins
					this.kNearNeighbour[indexOfFurthest] = currentTweet;
					tweetHasAdded = true;
				}
			}
		}
		
		
		
		
		/* Tri des k proches voisins */
		for(i = 0; i < k; i++){
			for(int j = 0; j < k; j++){
				if(this.wordDistance(this.kNearNeighbour[i].getContenu(), tweet) > this.wordDistance(this.kNearNeighbour[j].getContenu(), tweet)){
					Tweet aux = this.kNearNeighbour[i];
					this.kNearNeighbour[i] = this.kNearNeighbour[j];
					this.kNearNeighbour[j] = aux;
				}
			}
		}
		
		return this.vote(k);
	}
	
}
