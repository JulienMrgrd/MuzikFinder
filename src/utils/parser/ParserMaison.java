package utils.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserMaison {
	
	// http://stackoverflow.com/questions/40180378/how-to-remove-all-non-alphanumeric-except-dot-or-comma-between-2-digits
	private static final String THE_REGEX = "[^a-zA-Z0-9 .,']|(?<!\\d)[.,]|[.,](?!\\d)"; 
	
	public static Map<String, Integer> parserProcess(String lyrics, String langage){
		if(lyrics==null || lyrics.isEmpty() || langage==null || langage.isEmpty()) return null;
		Map<String, Integer> tags = null;
		List<String> excludeWords;
		try {
			excludeWords = (List<String>) getExcludedWords(langage);
			if(excludeWords==null || excludeWords.isEmpty()) return null;
			
			lyrics = lyrics.toLowerCase().replaceAll(THE_REGEX, " ");
			tags = new HashMap<String, Integer>();
			for(String s : lyrics.split(" ")){
				if(s != null && s.length()>=2 && !excludeWords.contains(s)){
					if(tags.containsKey(s)) tags.replace(s, tags.get(s)+1);
					else tags.put(s, 1);
				}
			}
		
		} catch (IOException e) {
			return null;
		}
		
		return tags;
	}
	
	private static List<String> getExcludedWords(String langue) throws IOException{
		BufferedReader br = null;
		List<String> motsAExclure;
		try{
			if(langue != null && !langue.isEmpty()) langue = langue.toLowerCase();
			else return null;
			br = new BufferedReader(new FileReader("./res/"+langue+"-exclusion.txt"));
		
			String line = null;
			motsAExclure = new ArrayList<String>();
			while ((line = br.readLine()) != null){
				motsAExclure.add(line);
			}
		} catch(FileNotFoundException exc) { 
			throw new FileNotFoundException("File not found" );  
		
		} catch(IOException ioe) { 
			throw new IOException("Erreur IO" ); 
		} finally {
			close(br);
		}
		return motsAExclure;
	}

	private static void close(BufferedReader br){
		if(br!=null){
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
