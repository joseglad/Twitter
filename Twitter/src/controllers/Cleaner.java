package controllers;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class Cleaner {
	
	/**
	 * Clean replace the part of the tweet which contains "reg" with c (c can be empty, so it delete all the "reg")
	 * @param tweet the tweet we want to clean
	 * @param reg the part of the tweet we want to clean
	 * @param c replaces the reg part of the tweet
	 * @return
	 */
	public static String clean(String tweet, String reg, String c){
		
		try{
			   Pattern p = Pattern.compile(reg);
			   Matcher m = p.matcher(tweet);
			   while (m.find())
			      return m.replaceAll(c);
			}
		catch(PatternSyntaxException pse){}
		
		return tweet;
		
	}
	
	/**
	 * clean our tweet in the way we want (delete all non desirable expressions)
	 * @param t the tweet we want to clean
	 * @return the tweet t all clean !
	 */
	public String cleanTweet(String t){
		
		String tweet = t;
		
		// Delete all words which begin with an @
		String reg = "@[a-zA-Z_0-9]* *";
		tweet = Cleaner.clean(tweet, reg, "");
		
		// Delete all words which begin with an #
		reg = "#[a-zA-Z_0-9]* *";
		tweet = Cleaner.clean(tweet, reg, "");
		
		// Delete all words which begin with an http://
		reg = "http://([a-zA-Z_0-9]|\\.|/)* *";
		tweet = Cleaner.clean(tweet, reg, "");
		
		// Delete all words which begin with an https://
		reg = "https://([a-zA-Z_0-9]|\\.|/)* *";
		tweet = Cleaner.clean(tweet, reg, "");
		
		// Delete special caracters
		reg = "[\"«»()?!\n:/,<>|]";
		tweet = Cleaner.clean(tweet, reg, "");
		
		// Replace all the dots with a space
		reg = "[.]";
		tweet = Cleaner.clean(tweet, reg, " ");
		
		// Delete all percentage
		reg = "[a-zA-Z_0-9]*%";
		tweet = Cleaner.clean(tweet, reg, "");
		
		return tweet;
	}
	
	
	
}
