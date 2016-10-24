package utils;

import java.util.ArrayList;
import java.util.List;

import nosql.mongo.MongoService;

public class test {
	
	public static void main(String[] args){
		
		MongoService ms = new MongoService(false);
		
		List<String> listTag = new ArrayList<>();
		listTag.add("flo");
		listTag.add("rida");
		
		System.out.println(ms.searchMusicsByTagsInTags(listTag) );
		
	}

}
