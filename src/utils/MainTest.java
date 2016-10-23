package utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;

import nosql.mongo.MongoService;
import server.dto.MusicDTO;

public class MainTest {

	public static void main (String []args){
		
		MongoService ms = new MongoService(false);
		List<String> listTag= new ArrayList<String>();
		listTag.add("red");
		listTag.add("light");
		System.out.println("Musique ayant red et light ==");
		for(MusicDTO mdto : ms.searchMusicsByTagsWithLyrics(listTag)){
			System.out.println("dans for "+mdto.getTrackId());
		}
	}
	
}
