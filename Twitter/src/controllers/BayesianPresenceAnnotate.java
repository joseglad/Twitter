package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import models.Tweet;

public class BayesianPresenceAnnotate {
	private BufferedReader reader;
	private String line;
	private ArrayList<Tweet> tweetList;
	private Double neutralN, positiveN, negativeN, N;
	private Double neutralWords, positiveWords, negativeWords;
	private String tweetDatabase;
	private int ngramme;
	private boolean debug = false;
	
	public BayesianPresenceAnnotate(String tweetDatabase, int n) {
		this.tweetDatabase = tweetDatabase;
		this.ngramme = n;
		this.tweetList = new ArrayList<Tweet>();
		this.neutralN = 0.0;
		this.positiveN = 0.0;
		this.negativeN = 0.0;
		this.N = 0.0;
		this.neutralWords = 0.0;
		this.positiveWords = 0.0;
		this.negativeWords = 0.0;
		this.initialiseTweetList(tweetDatabase);
		Iterator<Tweet> itTweet = tweetList.iterator();
		
		/* On compte le nombre de tweets, le nombre de positifs, negatifs et neutres et leur nombre de mots respectifs */
		while(itTweet.hasNext()) {
			this.N++;
			Tweet currentTweet = itTweet.next();
			if(currentTweet.getNotation().compareTo("0") == 0) {
				this.negativeN++;
				this.negativeWords += currentTweet.getContenu().trim().split(" ").length;
			}
			else {
				if(currentTweet.getNotation().compareTo("2") == 0) {
					this.neutralN++;
					this.neutralWords += currentTweet.getContenu().trim().split(" ").length;
				}
			
				else {
					if(currentTweet.getNotation().compareTo("4") == 0) {
						this.positiveN++;
						this.positiveWords += currentTweet.getContenu().trim().split(" ").length;
					}
				}
			}
		}
		if(this.debug){
			if((this.neutralN + this.positiveN + this.negativeN) == this.N) System.out.println("[BayesianPresenceAnnotate] L'initialisation semble correcte.");
			else System.out.println("[BayesianPresenceAnnotate] L'initialisation semble non correcte.");
		}
	}
	
	
	/**
	 * Initialise la liste de tweets avec la base de tweets
	 * @param tweetDatabase la base de tweets
	 */
	private void initialiseTweetList(String tweetDatabase){
		if(this.tweetList.iterator().hasNext() == false){
			
			try {
				this.reader = new BufferedReader(new FileReader(tweetDatabase));
			} catch(FileNotFoundException exc) {
				System.out.println("Erreur d'ouverture");
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
	 * Estime la proportion de tweets de la classe c dans l'ensemble de l'apprentissage
	 * @param c une classe parmi neutral, positive et negative
	 * @return la proportion
	 */
	public Double p(String c){
		if(c.toLowerCase().compareTo("neutral") == 0) return this.neutralN / this.N;
		if(c.toLowerCase().compareTo("positive") == 0) return this.positiveN / this.N;
		if(c.toLowerCase().compareTo("negative") == 0) return this.negativeN  / this.N;
		return 0.0;
	}
	
	/**
	 * Retourne le nombre total de mots des tweets de la classe c
	 * @param c une classe parmi neutral, positive et negative
	 * @return nombre total de mots dans c
	 */
	public Double n(String c) {
		if(c.toLowerCase().compareTo("neutral") == 0) return this.neutralWords;
		if(c.toLowerCase().compareTo("positive") == 0) return this.positiveWords;
		if(c.toLowerCase().compareTo("negative") == 0) return this.negativeWords;
		return 0.0;
	}
	
	
	/**
	 * Convertit une classe c en notation
	 * @param c
	 * @return notation
	 */
	private String classToNotation(String c) {
		if(c.toLowerCase().compareTo("neutral") == 0) return "2";
		if(c.toLowerCase().compareTo("positive") == 0) return "4";
		if(c.toLowerCase().compareTo("negative") == 0) return "0";
		return "";
	}
	
	
	/**
	 * Retourne le nombre d'occurence du mot m dans un texte de la classe c
	 * @param m mot
	 * @param c une classe parmi neutral, positive et negative
	 * @return le nombre
	 */
	public Double pn(String m, String c) {
		Double number = 0.0;
		String notation = this.classToNotation(c);
		Iterator<Tweet> itTweet = tweetList.iterator();
		
		while(itTweet.hasNext()) {
			Tweet currentTweet = itTweet.next();
			if(currentTweet.getNotation().compareTo(notation) == 0) {
				//String tab[] = currentTweet.getContenu().trim().split(" ");
				String tab[] = this.ngramme(currentTweet.getContenu(), this.ngramme);
				for(int i = 0; i < tab.length; i++) {
					if(tab[i].toUpperCase().compareTo(m.toUpperCase()) == 0) number ++;
				}
			}
			
		}
		if(this.debug) System.out.println(c+" Nombre de mot "+m+" "+number);
		return number;
	}
	
	/**
	 * Calcule la probabilité d'occurence du mot m dans un texte de classe c
	 * @param m un mot
	 * @param c une classe parmi neutral, positive et negative
	 * @return la probabilité
	 */
	public Double p(String m, String c) {
		return (this.pn(m, c) +1) / (this.n(c) + this.N);
	}
	
	/**
	 * 
	 * @param c
	 * @param t
	 * @return
	 */
	public Double p(String c, Tweet t) {
		Double productOfPMC = 1.0;
		//String[] words = t.getContenu().trim().split(" ");
		String[] words = this.ngramme(t.getContenu(), this.ngramme);
		String m;
		
		/* Calcul produit de la probabilité de tous les mots m de t */
		for(int i = 0; i < words.length; i++) {
			m = words[i];
			productOfPMC *= this.p(m, c);
		}
		return productOfPMC*this.p(c);
	}
	
	
	/**
	 * Retourne la notation du tweet t
	 * @param t le tweet
	 * @return la notation du tweet
	 */
	public int getNotation(Tweet t) {
		Double neg = p("negative", t);
		Double neu = p("neutral", t);
		Double pos = p("positive", t);
		
		if(neg > neu) {
			if(neg > pos) {
				return 0;
			}
			else {
				return 4;
			}
		}
		else {
			if(neu > pos) {
				return 2;
			}
			else {
				return 4;
			}
		}
	}
	
	/**
	 * Annote un tweet t en utilisant les unigrammes et bigrammes
	 * @param t un tweet
	 * @return l'annotation du tweet
	 */
	public int annoteByUnibigramme(Tweet t){
		BayesianPresenceAnnotate unigramme = new BayesianPresenceAnnotate(this.tweetDatabase, 1);
		BayesianPresenceAnnotate bigramme = new BayesianPresenceAnnotate(this.tweetDatabase, 2);
		Double neg = unigramme.p("negative", t) * bigramme.p("negative", t);
		Double neu = unigramme.p("neutral", t) * bigramme.p("neutral", t);
		Double pos = unigramme.p("positive", t) * bigramme.p("positive", t);
		
		if(neg > neu) {
			if(neg > pos) {
				return 0;
			}
			else {
				return 4;
			}
		}
		else {
			if(neu > pos) {
				return 2;
			}
			else {
				return 4;
			}
		}
	}
	
	/**
	 * Retourne un tableau contenant des n-grammes
	 * @param t un tweet
	 * @param n le nombre de mots dans le n-gramme
	 * @return les n-grammes
	 */
	public String[] ngramme(String t, int n) {
		String words[] = t.trim().split(" ");
		ArrayList<String> ngram = new ArrayList<String>();
		
		if(n < words.length) {
			for(int i = 0; i+n-1 < words.length; i++){
				String gram = "";
				for(int j = i; j < i+n; j++){
					gram += " "+words[j];
				}
				ngram.add(gram.trim());
			}
			String[] r = new String[ngram.size()];
			r = (String[]) ngram.toArray(r);
			return r;
		}
		return new String[]{t};
	}
	
	

}
