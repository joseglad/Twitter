package models;

public class Tweet {
	
	private String id; //The unique ID of a tweet
	private String utilisateur; //The user who posted the tweet
	private String contenu; //the contents
	private String date; //the date when it was posted
	private String requete; //the request emit to find this tweet
	private String notation; //the actual notation of that tweet
	
	/**
	 * Tweet constructor
	 * @param tweet 
	 */
	public Tweet(String tweet){
		String[] args = tweet.split(";"); //we divide each part of the tweet with that way !
		
		this.id = args[0];
		this.utilisateur = args[1];
		this.contenu = args[2];
		this.date = args[3];
		this.requete = args[4];
		this.notation = args[5];
	}
	
	
	public String getID(){
		return this.id;
	}
	
	public String getUtilisateur(){
		return this.utilisateur;
	}
	
	public String getContenu(){
		return this.contenu;
	}
	
	public String getDate(){
		return this.date;
	}
	
	public String getRequete(){
		return this.requete;
	}
	
	public String getNotation(){
		return this.notation;
	}
	
	
	public void setID(String id){
		this.id = id;
	}
	
	public void setUtilisateur(String u){
		this.utilisateur = u;
	}
	
	public void setContenu(String c){
		this.contenu = c;
	}
	
	public void setDate(String d){
		this.date = d;
	}
	
	public void setRequete(String r){
		this.requete = r;
	}
	
	public void setNotation(String n){
		this.notation = n;
	}
	
	public String getString(){
		String s = this.id+";"+this.utilisateur+";"+this.contenu+";"+this.date+";"+this.requete+";"+this.notation;
		return s;
	}
}
