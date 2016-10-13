package utils.textMining;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
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
	
	public static Set<String> getNounPhrases() {
		return nounPhrases;
	}

	public static void setNounPhrases(Set<String> nounPhrases) {
		ParserTest.nounPhrases = nounPhrases;
	}

	public static Set<String> getAdjectivePhrases() {
		return adjectivePhrases;
	}

	public static void setAdjectivePhrases(Set<String> adjectivePhrases) {
		ParserTest.adjectivePhrases = adjectivePhrases;
	}

	public static Set<String> getVerbPhrases() {
		return verbPhrases;
	}

	public static void setVerbPhrases(Set<String> verbPhrases) {
		ParserTest.verbPhrases = verbPhrases;
	}
	private static String line = "The Moon is a barren, rocky world without air and water. It has dark lava plain on its surface. " +
			"The Moon is filled wit craters. It has no light of its own. It gets its light from the Sun. The Moo keeps changing its " +
			"shape as it moves round the Earth. It spins on its axis in 27.3 days stars were named after the Edwin Aldrin were the " +
			"first ones to set their foot on the Moon on 21 July 1969 They reached the Moon in their space craft named Apollo II";
	
	public static void getNounPhrases(Parse p) {
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
		System.out.println("List of Noun Parse : "+nounPhrases);
		System.out.println("List of Adjective Parse : "+adjectivePhrases);
		System.out.println("List of Verb Parse : "+verbPhrases);
	}

}
