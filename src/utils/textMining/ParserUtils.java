package utils.textMining;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class ParserUtils {

	private static Set<String> parseSubprocess(Parse parse) {
		Set<String> words = new HashSet<>();
		String type = parse.getType();
		if (type.equals("NN") || type.equals("NNS") || type.equals("NNP") || type.equals("NNPS")  
				|| type.equals("JJ") || type.equals("JJR") || type.equals("JJS") || type.equals("VB") 
				|| type.equals("VBP") || type.equals("VBG")|| type.equals("VBD") || type.equals("VBN")){

			words.add(parse.getCoveredText());
		}

		for (Parse child : parse.getChildren()) {
			words.addAll( parseSubprocess(child) );
		}
		return words;
	}

	/**
	 * Crée le parsage et retourne la liste des mots
	 * @param text texte à parser
	 * @throws IOException 
	 */
	public static Set<String> parserProcess(String text, String language) throws Exception {
		InputStream is = null;
		switch (language) {
		case "en":
			is = new FileInputStream("res/en-parser-chunking.bin");
			break;

		default:
			return null;
		}
		
		ParserModel model = new ParserModel(is);
		Parser parser = ParserFactory.create(model);

		Set<String> words = new HashSet<>();

		Parse topParses[] = ParserTool.parseLine(text, parser, 1);
		for (Parse p : topParses){
			words.addAll(parseSubprocess(p));
		}
		return words;
	}


}
