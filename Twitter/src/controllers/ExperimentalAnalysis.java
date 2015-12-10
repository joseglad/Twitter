package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


import models.Tweet;


/**
 * Cette classe permet de calculer les taux d'erreur des différentes méthodes de classification de tweets
 * @author pansa
 *
 */
public class ExperimentalAnalysis {
	private ArrayList<Tweet> positiveTweets;
	private ArrayList<Tweet> neutralTweets;
	private ArrayList<Tweet> negativeTweets;
	private ArrayList<Tweet> A1;
	private ArrayList<Tweet> A2;
	private ArrayList<Tweet> A3;
	private BufferedReader reader;
	private String line;
	private int k = 3;
	private int NbSameTweetBySubSet = 100/3;
	private String crossValidation = "datas/crossvalidation.csv";
	private boolean debug = false;
	
	/**
	 * Prend en parametre une base de tweets de référence pour initialiser les 3 sous-ensembles A1, A2 et A3
	 * @param tweetDatabase la base de tweets de référence
	 */
	public ExperimentalAnalysis(String tweetDatabase) {
		this.initialiseTweetList(tweetDatabase);
		this.A1 = new ArrayList<Tweet>();
		this.A2 = new ArrayList<Tweet>();
		this.A3 = new ArrayList<Tweet>();
		this.setSubSets();
	}
	
	
	/**
	 * Initialise les 3 listes de tweets avec la base de tweets tweetDatabase
	 * @param tweetDatabase la base de tweets
	 */
	private void initialiseTweetList(String tweetDatabase){
		this.positiveTweets = new ArrayList<Tweet>();
		this.neutralTweets = new ArrayList<Tweet>();
		this.negativeTweets = new ArrayList<Tweet>();
		Tweet currentTweet;
		
			
		try {
			this.reader = new BufferedReader(new FileReader(tweetDatabase));
		} catch(FileNotFoundException exc) {
			System.out.println("Erreur d'ouverture "+tweetDatabase);
		}
			
		/* Ne fonctionne que si chaque tweet est sur une unique ligne */
		try {
			while((this.line = this.reader.readLine()) != null) {
				currentTweet = new Tweet(this.line);
				if(currentTweet.getNotation().compareTo("0") == 0) this.negativeTweets.add(new Tweet(this.line));
				if(currentTweet.getNotation().compareTo("2") == 0) this.neutralTweets.add(new Tweet(this.line));
				if(currentTweet.getNotation().compareTo("4") == 0) this.positiveTweets.add(new Tweet(this.line));
			}
		} catch(IOException e) {
			System.out.println("Erreur de lecture du ficher "+tweetDatabase);
		}
		catch(NullPointerException e) {
			System.out.println("NullPointerException  : Erreur de lecture du ficher "+tweetDatabase);
		}
	}
	
	/**
	 * Prépare le fichier "crossvalidation.csv" pour faire le test de validation croisée avec les tweets de set2 et set3
	 * @param set2 ensemble d'apprentissage
	 * @param set3 ensemble d'apprentissage
	 */
	private void setCrossValidation(ArrayList<Tweet> set2, ArrayList<Tweet> set3) {
		FileWriter writer;
		Iterator<Tweet> it = set2.iterator();
		int i = 0; 
		
		try {
			writer = new FileWriter(this.crossValidation, false);
			while(it.hasNext()) {
				Tweet tweet = it.next();
				writer.write(tweet.getString()+"\n");
				i++;				
			}
			
			writer.close();
		}
		catch(IOException e) {
			
		}
		
		it = set3.iterator();
		i = 0;
		try {
			writer = new FileWriter(this.crossValidation, true);
			while(it.hasNext()) {
				Tweet tweet = it.next();
				writer.write(tweet.getString()+"\n");
				i++;
			}
			
			writer.close();
		}
		catch(IOException e) {
			
		}
	}
	
	/** 
	 * Initialise les k sous-ensembles
	 * ICI k = 3
	 */
	public void setSubSets(){
		Iterator<Tweet> itPos = this.positiveTweets.iterator();
		Iterator<Tweet> itNeu = this.neutralTweets.iterator();
		Iterator<Tweet> itNeg = this.negativeTweets.iterator();
		int cpt = 0;
		
		while(itPos.hasNext() && itNeu.hasNext() && itNeg.hasNext() && (cpt < this.NbSameTweetBySubSet)) {
			this.A1.add(itPos.next());
			this.A1.add(itNeu.next());
			this.A1.add(itNeg.next());
			cpt++;
		}
		
		while(itPos.hasNext() && itNeu.hasNext() && itNeg.hasNext() && (cpt < (2*this.NbSameTweetBySubSet))) {
			this.A2.add(itPos.next());
			this.A2.add(itNeu.next());
			this.A2.add(itNeg.next());
			cpt++;
		}
		
		while(itPos.hasNext() && itNeu.hasNext() && itNeg.hasNext() && (cpt < (3*this.NbSameTweetBySubSet))) {
			this.A3.add(itPos.next());
			this.A3.add(itNeu.next());
			this.A3.add(itNeg.next());
			cpt++;
		}
	}
	
	
	/**
	 * Retourne le taux d'erreur E de Bayes par Presence N-gramme par rapport à la validité de l'annotation des sous-ensembles
	 * @param N represente le nombre de mots dans le N-gramme
	 * @return taux d'erreur E
	 */
	public Double anyliseBayesByPresenceNgramme(int N) {
		/* Un compteur pour compter le nombre de tweets mal annotés à chaque itération */
		Double E1 = 0.0, E2 = 0.0, E3 = 0.0;
		BayesianPresenceAnnotate bayesFr;
		Iterator<Tweet> it;
		int i = 0;
		
		/* On commence par l'annotation de l'ensemble A1 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A2, this.A3);
		bayesFr = new BayesianPresenceAnnotate(this.crossValidation, N);
		/* On annote les tweets A1 */
		it = this.A1.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.getNotation(tweet));
			/* Est ce que le tweet a la meme notation ??? */
			if(tweet.getNotation().compareTo(""+bayesFr.getNotation(tweet)) != 0) E1++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A2 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A3, this.A1);
		bayesFr = new BayesianPresenceAnnotate(this.crossValidation, N);
		/* On annote les tweets A2 */
		it = this.A2.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.getNotation(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.getNotation(tweet)) != 0) E2++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A3 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A1, this.A2);
		bayesFr = new BayesianPresenceAnnotate(this.crossValidation, N);
		/* On annote les tweets A3 */
		it = this.A3.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.getNotation(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.getNotation(tweet)) != 0) E3++;
			i++;
		}
		
		
		return (E1 + E2 + E3)/3;
	}
	
	
	/**
	 * Retourne le taux d'erreur E de Bayes par Presence Unibigramme par rapport à la validité de l'annotation des sous-ensembles
	 * @return taux d'erreur E
	 */
	public Double anyliseBayesByPresenceUnibigramme() {
		/* Un compteur pour compter le nombre de tweets mal annotés à chaque itération */
		Double E1 = 0.0, E2 = 0.0, E3 = 0.0;
		BayesianPresenceAnnotate bayesFr;
		Iterator<Tweet> it;
		int i = 0;
		int n = 0;
		
		/* On commence par l'annotation de l'ensemble A1 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A2, this.A3);
		bayesFr = new BayesianPresenceAnnotate(this.crossValidation, n);
		/* On annote les tweets A1 */
		it = this.A1.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.annoteByUnibigramme(tweet));
			/* Est ce que le tweet a la meme notation ??? */
			if(tweet.getNotation().compareTo(""+bayesFr.annoteByUnibigramme(tweet)) != 0) E1++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A2 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A3, this.A1);
		bayesFr = new BayesianPresenceAnnotate(this.crossValidation, n);
		/* On annote les tweets A2 */
		it = this.A2.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.annoteByUnibigramme(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.annoteByUnibigramme(tweet)) != 0) E2++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A3 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A1, this.A2);
		bayesFr = new BayesianPresenceAnnotate(this.crossValidation, n);
		/* On annote les tweets A3 */
		it = this.A3.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.annoteByUnibigramme(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.annoteByUnibigramme(tweet)) != 0) E3++;
			i++;
		}
		
		
		return (E1 + E2 + E3)/3;
	}
	
	
	/**
	 * Retourne le taux d'erreur E de Bayes par Frequency N-gramme par rapport à la validité de l'annotation des sous-ensembles
	 * @param N represente le nombre de mots dans le N-gramme
	 * @return taux d'erreur E
	 */
	public Double anyliseBayesByFrequencyNgramme(int N) {
		/* Un compteur pour compter le nombre de tweets mal annotés à chaque itération */
		Double E1 = 0.0, E2 = 0.0, E3 = 0.0;
		BayesianFrequencyAnnotate bayesFr;
		Iterator<Tweet> it;
		int i = 0;
		
		/* On commence par l'annotation de l'ensemble A1 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A2, this.A3);
		bayesFr = new BayesianFrequencyAnnotate(this.crossValidation, N);
		/* On annote les tweets A1 */
		it = this.A1.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.getNotation(tweet));
			/* Est ce que le tweet a la meme notation ??? */
			if(tweet.getNotation().compareTo(""+bayesFr.getNotation(tweet)) != 0) E1++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A2 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A3, this.A1);
		bayesFr = new BayesianFrequencyAnnotate(this.crossValidation, N);
		/* On annote les tweets A2 */
		it = this.A2.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.getNotation(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.getNotation(tweet)) != 0) E2++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A3 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A1, this.A2);
		bayesFr = new BayesianFrequencyAnnotate(this.crossValidation, N);
		/* On annote les tweets A3 */
		it = this.A3.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.getNotation(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.getNotation(tweet)) != 0) E3++;
			i++;
		}
		
		
		return (E1 + E2 + E3)/3;
	}
	
	/**
	 * Retourne le taux d'erreur E de Bayes par Frequency Unigramme Bigramme par rapport à la validité de l'annotation des sous-ensembles
	 * @return taux d'erreur E
	 */
	public Double anyliseBayesByFrequencyUnibigramme() {
		/* Un compteur pour compter le nombre de tweets mal annotés à chaque itération */
		Double E1 = 0.0, E2 = 0.0, E3 = 0.0;
		BayesianFrequencyAnnotate bayesFr;
		Iterator<Tweet> it;
		int i = 0;
		
		/* On commence par l'annotation de l'ensemble A1 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A2, this.A3);
		bayesFr = new BayesianFrequencyAnnotate(this.crossValidation, 0);
		/* On annote les tweets A1 */
		it = this.A1.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.annoteByUnibigramme(tweet));
			/* Est ce que le tweet a la meme notation ??? */
			if(tweet.getNotation().compareTo(""+bayesFr.annoteByUnibigramme(tweet)) != 0) E1++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A2 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A3, this.A1);
		bayesFr = new BayesianFrequencyAnnotate(this.crossValidation, 0);
		/* On annote les tweets A2 */
		it = this.A2.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.annoteByUnibigramme(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.annoteByUnibigramme(tweet)) != 0) E2++;
			i++;
		}
		
		i = 0;
		/* On commence par l'annotation de l'ensemble A3 */
		/* On crée la base d'apprentissage */
		this.setCrossValidation(this.A1, this.A2);
		bayesFr = new BayesianFrequencyAnnotate(this.crossValidation, 0);
		/* On annote les tweets A3 */
		it = this.A3.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+bayesFr.annoteByUnibigramme(tweet));
			if(tweet.getNotation().compareTo(""+bayesFr.annoteByUnibigramme(tweet)) != 0) E3++;
			i++;
		}
		
		
		return (E1 + E2 + E3)/3;
	}
	
	
	/**
	 * Retourne le taux d'erreur E de Key Words par rapport à la validité de l'annotation des sous-ensembles
	 * @return taux d'erreur E
	 */
	public Double anyliseKeyWords() {
		/* Un compteur pour compter le nombre de tweets mal annotés à chaque itération */
		Double E1 = 0.0, E2 = 0.0, E3 = 0.0;
		KeyWordsAnnotate keyWords;
		Iterator<Tweet> it;
		int i = 0;
		
		keyWords = new KeyWordsAnnotate();
		/* On annote les tweets A1 */
		it = this.A1.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+keyWords.analyse(tweet.getContenu()));
			/* Est ce que le tweet a la meme notation ??? */
			if(tweet.getNotation().compareTo(""+keyWords.analyse(tweet.getContenu())) != 0) E1++;
			i++;
		}
		
		i = 0;
		/* On annote les tweets A2 */
		it = this.A2.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+keyWords.analyse(tweet.getContenu()));
			if(tweet.getNotation().compareTo(""+keyWords.analyse(tweet.getContenu())) != 0) E2++;
			i++;
		}
		
		i = 0;
		/* On annote les tweets A3 */
		it = this.A3.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+keyWords.analyse(tweet.getContenu()));
			if(tweet.getNotation().compareTo(""+keyWords.analyse(tweet.getContenu())) != 0) E3++;
			i++;
		}
		
		
		return (E1 + E2 + E3)/3;
	}
	
	/**
	 * Retourne le taux d'erreur E de KNN par rapport à la validité de l'annotation des sous-ensembles
	 * @return taux d'erreur E
	 */
	public Double anyliseKNN() {
		/* Un compteur pour compter le nombre de tweets mal annotés à chaque itération */
		Double E1 = 0.0, E2 = 0.0, E3 = 0.0;
		KNNAnnotate knn;
		int k = 10;
		Iterator<Tweet> it;
		int i = 0;
		
		knn = new KNNAnnotate();
		/* On annote les tweets A1 */
		it = this.A1.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+knn.knnAnalyse(tweet.getContenu(), this.crossValidation, k));
			/* Est ce que le tweet a la meme notation ??? */
			if(tweet.getNotation().compareTo(""+knn.knnAnalyse(tweet.getContenu(), this.crossValidation, k)) != 0) E1++;
			i++;
		}
		
		i = 0;
		/* On annote les tweets A2 */
		it = this.A2.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+knn.knnAnalyse(tweet.getContenu(), this.crossValidation, k));
			if(tweet.getNotation().compareTo(""+knn.knnAnalyse(tweet.getContenu(), this.crossValidation, k)) != 0) E2++;
			i++;
		}
		
		i = 0;
		/* On annote les tweets A3 */
		it = this.A3.iterator();
		while((i < this.NbSameTweetBySubSet) && it.hasNext()) {
			Tweet tweet = it.next();
			if(this.debug) System.out.println(i+" "+knn.knnAnalyse(tweet.getContenu(), this.crossValidation, k));
			if(tweet.getNotation().compareTo(""+knn.knnAnalyse(tweet.getContenu(), this.crossValidation, k)) != 0) E3++;
			i++;
		}
		
		
		return (E1 + E2 + E3)/3;
	}
}
