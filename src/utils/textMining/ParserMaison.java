package utils.textMining;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nosql.mongo.MongoService;

public class ParserMaison {
	
	private static String textTest = "The Moon  is  a barren, rocky world without air and water. It has dark lava plain on its surface. " +
			"The Moon is filled wit craters. It has no light of its own. It gets its light from the Sun. The Moo keeps changing its " +
			"shape as it moves round the Earth. It spins on its axis in 27.3 days stars were named after the Edwin Aldrin were the " +
			"first ones to set their foot on the Moon on 21 July 1969 They reached the Moon in their space craft named Apollo II he's" +
			"\n...\n\n******* This Lyrics is NOT for Commercial use *******\n(1409613097569)";
	
	public static ArrayList<String> parserProcess(String lyric, String langage){
		ArrayList<String> tags = new ArrayList<String>();
		ArrayList<String> motExclure;
		try {
			motExclure = getMotAExlure(langage);
			lyric = lyric.toLowerCase().substring(0, lyric.length()-75).replaceAll("[^a-zA-Z0-9 .,']|(?<!\\d)[.,]|[.,](?!\\d)", " ");
			for(String s : lyric.split(" ")){
				if(s != null && s.length()>=2 ){
					if(!motExclure.contains(s)){
						if(!tags.contains(s)){
							tags.add(s);
						}
					}
				}
			}
		
		} catch (IOException e) {
			return tags;
		}
		
		
		return tags;
		
	}
	
	public static ArrayList<String> getMotAExlure(String langue) throws IOException{
		BufferedReader br = null;
		ArrayList<String> motsAExclure = new ArrayList<String>();
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
	
	
	public static List<String> toto(){
		String withOutWordExclude = new String();
		ArrayList<String> list = new ArrayList<>();
		try {
			ArrayList<String> a = getMotAExlure("en");
			textTest = textTest.toLowerCase().replaceAll("[^a-z0-9 ]", "");
			for(String s : textTest.split(" ")){
				if(s.length()>=2){
					if(!a.contains(s)){
						if(!withOutWordExclude.contains(s)){
							withOutWordExclude+=s+" ";
							list.add(s);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println((withOutWordExclude));
		System.out.println(withOutWordExclude.split(" ").length);
		return list;
	}
	
	public static void main(String[] args){
		/*List<String> la = parserProcess(textTest, "en");
		
		for(String s : la){
			System.out.println(s);
		}*/
		
		MongoService m = new MongoService(false);
		ArrayList<String> listTag = new ArrayList<>(4);
		listTag.add("look");
		listTag.add("i'm");
		listTag.add("cool");
		/*System.out.println(m.searchMusicsByTagsInTags(listTag).size());
		for(MusicDTO s : m.searchMusicsByTagsInTags(listTag)){
			System.out.println(s);
		} Ne pas lancer pour l'instant lance une exception*/
	}
	
}
