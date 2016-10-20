package utils.textMining;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ParserMaison {
	
	private static String textTest = "The Moon is a barren, rocky world without air and water. It has dark lava plain on its surface. " +
			"The Moon is filled wit craters. It has no light of its own. It gets its light from the Sun. The Moo keeps changing its " +
			"shape as it moves round the Earth. It spins on its axis in 27.3 days stars were named after the Edwin Aldrin were the " +
			"first ones to set their foot on the Moon on 21 July 1969 They reached the Moon in their space craft named Apollo II";
	
	
	public static ArrayList<String> getMotAExlure(String langue) throws IOException{
		BufferedReader br = null;
		ArrayList<String> motsAExclure = new ArrayList<String>();
		System.out.println("ici");
		try{
			br = new BufferedReader(new FileReader("./res/"+langue+"-exclusion.txt"));
		} catch(FileNotFoundException exc) { throw new FileNotFoundException("File not found" );  }
		
		String line = null; 
		try {
			while ((line = br.readLine()) != null)
			{
				motsAExclure.add(line);
			}
		} catch(IOException ioe) { throw new IOException("Erreur IO" ); }
		
		close(br);
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
	
	
	public static void main(String[]args){
		String withOutWordExclude = new String();
		try {
			ArrayList<String> a = getMotAExlure("en");
			for(String s : a){
				System.out.println(s);
			}
			textTest = textTest.toLowerCase().replaceAll("[^A-Za-z0-9 ]", "");
			for(String s : textTest.split(" ")){
				if(!a.contains(s)){
					if(!withOutWordExclude.contains(s)){
						withOutWordExclude+=s+" ";
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println((withOutWordExclude));
		System.out.println(withOutWordExclude.split(" ").length);
		
	}
	
	
}
