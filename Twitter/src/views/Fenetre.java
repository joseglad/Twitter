package views;

import javax.swing.*;

import controllers.AnnoterByBayesianFrequency;
import controllers.AnnoterByBayesianPresence;
import controllers.AnnoterByKNN;
import controllers.AnnoterByKeyWords;
import controllers.CounterByClassOfTweet;
import controllers.ExperimentalAnalysis;
import controllers.JTableModel;
import controllers.SaveDatas;
import controllers.Search;
import controllers.TransferDatas;

import models.Tweet;


import twitter4j.TwitterException;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
 

public class Fenetre extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel verifyText = new JLabel("Nombre de requètes restantes : ");
	private JTextField verifyResult = new JTextField("");
	private JButton verifyButton = new JButton("Vérifier");
	private JCheckBox proxyCheck = new JCheckBox("Connexion via un proxy", false);
	private JTextField searchField = new JTextField("");
	private JButton searchButton = new JButton("Search");
	private JPanel tweets = new JPanel();
	private JButton saveButton = new JButton("Sauvegarder le résultat");
	private JButton transferButton = new JButton("Transfert dans base de référence");
	private Search search = new Search();
	private SaveDatas saver;
	private JTable tableau = new JTable(new JTableModel(new ArrayList<Tweet>()));
	private JScrollPane scroll;
	private JButton keyWordsButton = new JButton("KeyWords");
	private JButton knnButton = new JButton("KNN");
	private JButton BayesPresenceButton = new JButton("Bayes by presence");
	private JButton BayesFrequencyUnigrammeButton = new JButton("Bayes by frequency - Unigramme");
	private JButton BayesFrequencyBigrammeButton = new JButton("Bayes by frequency - Bigramme");
	private JButton BayesFrequencyUnibigrammeButton = new JButton("Bayes by frequency - Unibigramme");
	
	private JFrame graphe = new JFrame();
	private boolean debug = false;
	
	
	/**
	 * Construit une interface graphique contenant tous les composants pour analyser de tweets
	 * @throws TwitterException pour indiquer qu'il y a eu un problème dans l'API Twitter
	 */
	public Fenetre() throws TwitterException {
		this.setTitle("Analyseur de comportement - Twitter");
		this.setSize(1300, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);

		JPanel pan1 = new JPanel();
		JPanel pan2 = new JPanel();
		JPanel pan3 = new JPanel();
		JPanel pan4 = new JPanel();
		JPanel pan5 = new JPanel();
		JPanel panelContener = new JPanel();

		pan1.setLayout(new BoxLayout(pan1, BoxLayout.LINE_AXIS));
		pan1.add(this.verifyText);
		this.verifyResult.setColumns(1);
		pan1.add(this.verifyResult);
		this.verifyButton.addActionListener(new VerifyListener());
		pan1.add(this.verifyButton);

		pan2.setLayout(new BoxLayout(pan2, BoxLayout.LINE_AXIS));
		this.proxyCheck.addItemListener(new ItemListener() {
		      public void itemStateChanged(ItemEvent e) {
		          System.out.println("Checked? " + proxyCheck.isSelected());
		          if(proxyCheck.isSelected()) {
		        	  search.setProxyConfiguration();
		          } else {
		        	  search.setConfigurationBuilder();
		          }
		        }
		      });
		pan2.add(this.proxyCheck);

		pan3.setLayout(new BoxLayout(pan3, BoxLayout.LINE_AXIS));
		Font police = new Font("Arial", Font.BOLD, 14);
		this.searchField.setFont(police);
		this.searchField.setPreferredSize(new Dimension(15, 3));
		pan3.add(this.searchField);
		this.searchButton.addActionListener(new SearchListener());
		pan3.add(this.searchButton);

		this.scroll = new JScrollPane(this.tableau);
		this.scroll.setSize(700,500);
		this.tweets.add(scroll, BorderLayout.WEST);
		this.tweets.setLayout(new BoxLayout(this.tweets, BoxLayout.LINE_AXIS));
		
		pan4.setLayout(new BoxLayout(pan4, BoxLayout.LINE_AXIS));
		this.keyWordsButton.addActionListener(new KeyWordsListener());
		this.knnButton.addActionListener(new KNNListener());
		this.BayesPresenceButton.addActionListener(new BayesianPresenceListener());
		this.BayesFrequencyUnigrammeButton.addActionListener(new BayesianFrequencyUnigrammeListener());
		this.BayesFrequencyBigrammeButton.addActionListener(new BayesianFrequencyBigrammeListener());
		this.BayesFrequencyUnibigrammeButton.addActionListener(new BayesianFrequencyUnibigrammeListener());
		pan4.add(this.keyWordsButton);
		pan4.add(this.knnButton);
		pan4.add(this.BayesPresenceButton);
		pan4.add(this.BayesFrequencyUnigrammeButton);
		pan4.add(this.BayesFrequencyBigrammeButton);
		pan4.add(this.BayesFrequencyUnibigrammeButton);

		pan5.setLayout(new BoxLayout(pan5, BoxLayout.LINE_AXIS));
		this.saveButton.addActionListener(new SaveListener());
		this.transferButton.addActionListener(new TransferListener());
		pan5.add(this.saveButton);
		pan5.add(this.transferButton);

		panelContener.setLayout(new BoxLayout(panelContener, BoxLayout.PAGE_AXIS));
		panelContener.add(pan1);
		panelContener.add(pan2);
		panelContener.add(pan3);
		panelContener.add(this.tweets);
		panelContener.add(pan4);		
		panelContener.add(pan5);		
		
		this.getContentPane().add(panelContener);
		this.setVisible(true);
	}
	
	/**
	 * Rend visible l'interface graphique générale
	 */
	public void setVisible() {
		this.setVisible(true);
	}
	
	/**
	 * Affiche l'interface graphique de la répartion des polarités des tweets
	 */
	public void displayGraph() {
		CounterByClassOfTweet cpt = new CounterByClassOfTweet("datas/newtweets.csv");
		graphe.setSize(400, 300);
		double[] values = new double[3];
	    String[] names = new String[3];
	    values[0] = cpt.getNegativeTweetNumber();
	    names[0] = "Négatif ("+cpt.getNegativeTweetNumber()+"/"+cpt.getTweetNumber()+")";

	    values[1] = cpt.getNeutralTweetNumber();
	    names[1] = "Neutre ("+cpt.getNeutralTweetNumber()+"/"+cpt.getTweetNumber()+")";

	    values[2] = cpt.getPositiveTweetNumber();
	    names[2] = "Positif ("+cpt.getPositiveTweetNumber()+"/"+cpt.getTweetNumber()+")";

	    graphe.getContentPane().add(new ChartPanel(values, names, "Répartition des polarités"));
	    WindowListener wndCloser = new WindowAdapter() {
		      public void windowClosing(WindowEvent e) {
		    	  System.exit(0);
		      }
	    };
		graphe.addWindowListener(wndCloser);
		graphe.setVisible(true);
	}
	
	/**
	 * Action permettant la vérification du nombre de tweets restants
	 * @author pansa
	 *
	 */
	class VerifyListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Clic sur le bouton Vérifier");
			verifyResult.setText(""+search.getRateLimit());
		}
	}
	
	/**
	 * Action lançant la recherche de tweets
	 * @author pansa
	 *
	 */
	class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Clic sur le bouton Search");
			System.out.println("Recherche de la chaine de caractères : ");
			
			try {
				if(searchField.getText() != "") {
					System.out.println(searchField.getText());
					search.search(searchField.getText());
					search.getTweets(searchField.getText());
					tweets.remove(scroll);
					tableau = new JTable(new JTableModel(search.getListOfTweets()));
					setColumnsOfTable();
					scroll = new JScrollPane(tableau);
					tweets.add(scroll);
					pack();
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Action lançant la sauvegarde des tweets
	 * @author pansa
	 *
	 */
	class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Clic sur le bouton Sauvegarder");
			saver = new SaveDatas();
			saver.save();
		}
	}
	
	/**
	 * Action lançant le transfert des tweets dans la base de référence
	 * @author pansa
	 *
	 */
	class TransferListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			System.out.println("Clic sur le bouton Transfert");
			TransferDatas trans = new TransferDatas();
			trans.transfer();
		}
	}
	
	/**
	 * Action annotant les tweets par le classifieur KeyWords
	 * @author pansa
	 *
	 */
	class KeyWordsListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			AnnoterByKeyWords annoter = new AnnoterByKeyWords();			
			
			System.out.println("Classification par KeyWords");
			/* Appel à getRowCount() pour régler le problème de sauvegarde sur le fichier */
			tableau.getModel().getRowCount();
			tweets.remove(scroll);
			tableau = new JTable(new JTableModel(annoter.getTweets()));
			setColumnsOfTable();
			scroll = new JScrollPane(tableau);
			tweets.add(scroll);
			pack();
			
			displayGraph();			
		}
	}
	
	/**
	 * Action annotant les tweets par le classifieur KNN
	 * @author pansa
	 *
	 */
	class KNNListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int k = 10;
			AnnoterByKNN annoter = new AnnoterByKNN(k);
			System.out.println("Classification par KNN");
			/* Appel à getRowCount() pour régler le problème de sauvegarde sur le fichier */
			tableau.getModel().getRowCount();
			tweets.remove(scroll);
			tableau = new JTable(new JTableModel(annoter.getTweets()));
			setColumnsOfTable();
			scroll = new JScrollPane(tableau);
			tweets.add(scroll);
			pack();
			
			displayGraph();
		}
	}
	
	
	/**
	 * Action annotant les tweets par le classifieur Bayes par Présence
	 * @author pansa
	 *
	 */
	class BayesianPresenceListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			AnnoterByBayesianPresence annoter = new AnnoterByBayesianPresence();
			System.out.println("Classification par BayesianPresence");
			/* Appel à getRowCount() pour régler le problème de sauvegarde sur le fichier */
			tableau.getModel().getRowCount();
			tweets.remove(scroll);
			tableau = new JTable(new JTableModel(annoter.getTweets()));
			setColumnsOfTable();
			scroll = new JScrollPane(tableau);
			tweets.add(scroll);
			pack();
			
			displayGraph();
		}
	}
	
	/**
	 * Action annotant les tweets par le classifieur Bayes par Fréquence Unigramme
	 * @author pansa
	 *
	 */
	class BayesianFrequencyUnigrammeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int n = 1;
			AnnoterByBayesianFrequency annoter = new AnnoterByBayesianFrequency(n);
			System.out.println("Classification par BayesianFrequencyUnigramme");
			/* Appel à getRowCount() pour régler le problème de sauvegarde sur le fichier */
			tableau.getModel().getRowCount();
			tweets.remove(scroll);
			tableau = new JTable(new JTableModel(annoter.getTweets()));
			setColumnsOfTable();
			scroll = new JScrollPane(tableau);
			tweets.add(scroll);
			pack();
			
			displayGraph();
		}
	}
	
	/**
	 * Action annotant les tweets par le classifieur Bayes par Fréquence Bigramme
	 * @author pansa
	 *
	 */
	class BayesianFrequencyBigrammeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			int n = 2;
			AnnoterByBayesianFrequency annoter = new AnnoterByBayesianFrequency(n);
			System.out.println("Classification par BayesianFrequencyBigramme");
			/* Appel à getRowCount() pour régler le problème de sauvegarde sur le fichier */
			tableau.getModel().getRowCount();
			tweets.remove(scroll);
			tableau = new JTable(new JTableModel(annoter.getTweets()));
			setColumnsOfTable();
			scroll = new JScrollPane(tableau);
			tweets.add(scroll);
			pack();
			
			displayGraph();
		}
	}
	
	/**
	 * Action annotant les tweets par le classifieur Bayes par Fréquence Unigramme Bigramme
	 * @author pansa
	 *
	 */
	class BayesianFrequencyUnibigrammeListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			/* A corriger */
			int n = 0;
			AnnoterByBayesianFrequency annoter = new AnnoterByBayesianFrequency(n);
			System.out.println("Classification par BayesianFrequencyUnibigramme");
			/* Appel à getRowCount() pour régler le problème de sauvegarde sur le fichier */
			tableau.getModel().getRowCount();
			tweets.remove(scroll);
			tableau = new JTable(new JTableModel(annoter.getTweetsByUnibigramme()));
			setColumnsOfTable();
			scroll = new JScrollPane(tableau);
			tweets.add(scroll);
			pack();
			
			displayGraph();
		}
	}
	
	/**
	 * Fixe la taille des colonnes du tableau
	 */
	private void setColumnsOfTable() {
		tableau.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableau.getColumnModel().getColumn(1).setPreferredWidth(200);
		tableau.getColumnModel().getColumn(2).setPreferredWidth(1000);
		tableau.getColumnModel().getColumn(3).setPreferredWidth(300);
		tableau.getColumnModel().getColumn(4).setPreferredWidth(200);
		tableau.getColumnModel().getColumn(5).setPreferredWidth(200);
	}

	/**
	 * Unique méthode MAIN lançant l'interface graphique
	 * @param args
	 * @throws TwitterException
	 */
	public static void main(String[] args) throws TwitterException {
		Fenetre fen = new Fenetre();
		fen.setVisible();
			    
	    ExperimentalAnalysis ana = new ExperimentalAnalysis("datas/tweetreference.csv");
	    System.out.println("## Analyse expérimentale : pour (environ) 100 tweets dans chaque sous-ensemble ##");
	    System.out.println("Estimation des taux d'erreur par une validation croisée");
		System.out.println("[Bayes] Présence, unigramme : "+ana.anyliseBayesByPresenceNgramme(1)+" %");
		System.out.println("[Bayes] Présence, bigramme : "+ana.anyliseBayesByPresenceNgramme(2)+" %");
		System.out.println("[Bayes] Présence, unigramme + bigramme : "+ana.anyliseBayesByPresenceUnibigramme()+" %");
		System.out.println("[Bayes] Fréquence, unigramme : "+ana.anyliseBayesByFrequencyNgramme(1)+" %");
		System.out.println("[Bayes] Fréquence, bigramme : "+ana.anyliseBayesByFrequencyNgramme(2)+" %");
		System.out.println("[Bayes] Fréquence, unigramme + bigramme : "+ana.anyliseBayesByFrequencyUnibigramme()+" %");
		System.out.println("[KeyWords] : "+ana.anyliseKeyWords()+" %");
		System.out.println("[KNN] : "+ana.anyliseKNN()+" %");
		System.out.println("Les classifieurs KeyWords et KNN utilisent par défaut les unigrammes.");
		System.out.println("Les aspects bigramme et unigramme+bigramme n'ont pas été traité.");
  	}
}