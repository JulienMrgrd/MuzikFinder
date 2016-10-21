package utils.textMining;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class ParserTest {

	static Set<String> nounPhrases = new HashSet<>();
	static Set<String> adjectivePhrases = new HashSet<>();
	static Set<String> verbPhrases = new HashSet<>();
	
	private static String line = "The Moon is a barren, rocky world without air and water. It has dark lava plain on its surface. " +
			"The Moon is filled wit craters. It has no light of its own. It gets its light from the Sun. The Moo keeps changing its " +
			"shape as it moves round the Earth. It spins on its axis in 27.3 days stars were named after the Edwin Aldrin were the " +
			"first ones to set their foot on the Moon on 21 July 1969 They reached the Moon in their space craft named Apollo II";
	
	public void getNounPhrases(Parse p) {
	    if (p.getType().equals("NN") || p.getType().equals("NNS") || p.getType().equals("NNP") || p.getType().equals("NNPS")) {
	         nounPhrases.add(p.getCoveredText());
	         //System.out.println(p.getCoveredText());
	    }
	    
	    if (p.getType().equals("JJ") || p.getType().equals("JJR") || p.getType().equals("JJS")) {
	    	adjectivePhrases.add(p.getCoveredText());
	         //System.out.println(p.getCoveredText());
	    }
	    
	    if (p.getType().equals("VB") || p.getType().equals("VBP") || p.getType().equals("VBG")|| p.getType().equals("VBD") || p.getType().equals("VBN")) {
	    	verbPhrases.add(p.getCoveredText());
	         //System.out.println(p.getCoveredText());
	    }
	    
	    for (Parse child : p.getChildren()) {
	         getNounPhrases(child);
	    }
	}
	
	
	public void parserAction() throws Exception {
		InputStream is = new FileInputStream("res/en-parser-chunking.bin");
	    ParserModel model = new ParserModel(is);
	    Parser parser = ParserFactory.create(model);
	    Parse topParses[] = ParserTool.parseLine(line, parser, 1);
	    for (Parse p : topParses){
	    	//p.show();
	    	getNounPhrases(p);
	    }
	}
	public static void main(String[] args) throws Exception {
		new ParserTest().parserAction();
		System.out.println("Size = "+nounPhrases.size()+" List of Noun Parse : "+nounPhrases);
		System.out.println("Size = "+adjectivePhrases.size()+" List of Adjective Parse : "+adjectivePhrases);
		System.out.println("Size = "+verbPhrases.size()+" List of Verb Parse : "+verbPhrases);
		ArrayList<String> parserTest = new ArrayList<>();
		parserTest.addAll(nounPhrases);
		parserTest.addAll(adjectivePhrases);
		parserTest.addAll(verbPhrases);
		for(int i=0; i<parserTest.size(); i++){
			parserTest.set(i, parserTest.get(i).toLowerCase().replaceAll("[^a-z0-9 ]", ""));
		}
		List<String> list = ParserMaison.toto();
		List<String> listCopy = new ArrayList<>();
		listCopy.addAll(list);
		for(String s : list){
			if(parserTest.contains(s)){
				listCopy.remove(s);
			}
		}
		System.out.println(listCopy);
		
	}

}
