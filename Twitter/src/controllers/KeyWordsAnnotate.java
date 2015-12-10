package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class KeyWordsAnnotate {
	private ArrayList<String> negativeWords;
	private ArrayList<String> positiveWords;
	private int nnegative = 0;
	private int npositive = 0;
	private BufferedReader reader;
	private String line;
	private Boolean debug = false;
	
	/**
	 * Builder for the keyword technic, we initialize the negative and negative Arrays with the files 
	 */
	public KeyWordsAnnotate(){
		this.negativeWords = new ArrayList<String>();
		this.positiveWords = new ArrayList<String>();
		
		try {
			this.reader = new BufferedReader(new FileReader("datas/negative.txt"));
		} catch(FileNotFoundException exc) {
			System.out.println("Erreur d'ouverture negative.txt");
		}
		try {
			while((this.line = this.reader.readLine()) != null) {
				String[] array = this.line.split(",");
				for(int i = 0; i < array.length; i++){
					if(array[i].trim().compareTo(" ") == 0) continue;
					this.negativeWords.add(array[i].trim());
				}
			}
		} catch(IOException e) {
			System.out.println("Erreur de lecture du ficher negative.txt");
		}
		
		try {
			this.reader = new BufferedReader(new FileReader("datas/positive.txt"));
		} catch(FileNotFoundException exc) {
			System.out.println("Erreur d'ouverture positive.txt");
		}
		try {
			while((this.line = this.reader.readLine()) != null) {
				String[] array = this.line.split(",");
				for(int i = 0; i < array.length; i++){
					if(array[i].trim().compareTo(" ") == 0) continue;
					this.positiveWords.add(array[i].trim());
				}
			}
		} catch(IOException e) {
			System.out.println("Erreur de lecture du ficher positive.txt");
		}
	}
	
	/**
	 * Vérifie si le mot word appartient à la liste de mots wordsArray
	 * @param wordsArray la liste de mots
	 * @param word un mot
	 * @return retourne true si c'est le cas false sinon
	 */
	public Boolean analyseWord(ArrayList<String> wordsArray, String word){
		Iterator<String> it = wordsArray.iterator();
		while(it.hasNext()){
			if(word.toUpperCase().compareTo(it.next().toUpperCase()) == 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Détermine la polarité d'un tweet
	 * @param tweet un tweet
	 * @return retourne 0 pour negatif, 2 pour neutre et 4 pour positif
	 */
	public int analyse(String tweet){		
		String[]tweetArray = tweet.trim().split(" ");
		this.nnegative = 0;
		this.npositive = 0;
		
		
		for(int i = 0; i < tweetArray.length; i++){
			if(this.debug) System.out.println(i+" "+tweetArray[i]);
			
			if(tweetArray[i].compareTo(" ") == 0) continue;
			
			if(analyseWord(this.negativeWords, tweetArray[i])) {
				if(this.debug) System.out.println("Négatif: "+tweetArray[i]);
				this.nnegative++;
			}
			else {
				if(analyseWord(this.positiveWords, tweetArray[i])) {
					if(this.debug) System.out.println("Positif: "+tweetArray[i]);
					this.npositive++; 
				}
			}
		}
		
		if(this.debug){
			System.out.println("Nombre de mots negatifs: "+this.nnegative);
			System.out.println("Nombre de mots positifs: "+this.npositive);
		}
		
		if(this.nnegative > this.npositive) return 0;
		if(this.npositive > this.nnegative) return 4;
		else return 2;
	}
}