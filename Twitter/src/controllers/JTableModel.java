package controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import models.Tweet;

public class JTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Tweet> tweets;
    private final String[] entetes = {"ID Tweet","Utilisateur","Contenu","Date","Requête","Notation"};
 
    public JTableModel(List<Tweet> tweets){
        super();
        this.tweets = tweets;
	
 	}
 
    public int getRowCount() {
    	this.saveTweets();
        return tweets.size();
    }
 
    public int getColumnCount() {
        return entetes.length;
    }
 
    public String getColumnName(int columnIndex) {
        return entetes[columnIndex];
    }
 
    public Object getValueAt(int rowIndex, int columnIndex) {
    	switch(columnIndex){
    	case 0:
    		return tweets.get(rowIndex).getID();
    	case 1:
    		return tweets.get(rowIndex).getUtilisateur();
    	case 2:
    		return tweets.get(rowIndex).getContenu();
    	case 3:
    		return tweets.get(rowIndex).getDate();
    	case 4:
    		return tweets.get(rowIndex).getRequete();
    	case 5:
    		return tweets.get(rowIndex).getNotation();
    	default:
    		return null;
    	}
    }
    
    @Override
    /**
     * Seule la colonne 5 Notation est éditable
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 5;
    }
     
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if(value != null){
            Tweet tweet = tweets.get(rowIndex);
     
            switch(columnIndex){
                case 0:
                    tweet.setID((String)value);
                    break;
                case 1:
                    tweet.setUtilisateur((String)value);
                    break;
                case 2:
                    tweet.setContenu((String)value);
                    break;
                case 3:
                    tweet.setDate((String)value);
                    break;
                case 4:
                    tweet.setRequete((String)value);
                    break;
                case 5:
                    tweet.setNotation((String)value);
                    break;
            }
            this.saveTweets();
        }
    }
    
    
   
	
	@SuppressWarnings("unchecked")
	@Override
    public Class getColumnClass(int columnIndex){
    	return getValueAt(0, columnIndex).getClass();
        
    }
	
    
	public void saveTweets(){
		File monfichier = new File("datas/newtweets.csv");
		String contenu = "";
		
		Iterator<Tweet> it = tweets.iterator();
		while(it.hasNext()){
			contenu += it.next().getString()+"\n";
		}
		
		
		try {
			FileWriter writer = new FileWriter(monfichier, false);
			writer.write(contenu);
			
			writer.close();
		}
		catch(IOException e) {
			
		}
	}
    
}
