package utils.textMining;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserMaison {
	
	// http://stackoverflow.com/questions/40180378/how-to-remove-all-non-alphanumeric-except-dot-or-comma-between-2-digits
	private static final String THE_REGEX = "[^a-zA-Z0-9 .,']|(?<!\\d)[.,]|[.,](?!\\d)"; 
	
	public static List<String> parserProcess(String lyrics, String langage){
		if(lyrics==null || lyrics.isEmpty() || langage==null || langage.isEmpty()) return null;
		List<String> tags = null;
		List<String> excludeWords;
		try {
			excludeWords = (ArrayList<String>) getExcludedWords(langage);
			if(excludeWords==null || excludeWords.isEmpty()) return null;
			
			lyrics = lyrics.toLowerCase().replaceAll(THE_REGEX, " ");
			tags = new ArrayList<String>();
			for(String s : lyrics.split(" ")){
				if(s != null && s.length()>=2 && !excludeWords.contains(s) && !tags.contains(s)){
					tags.add(s);
				}
			}
		
		} catch (IOException e) {}
		
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
