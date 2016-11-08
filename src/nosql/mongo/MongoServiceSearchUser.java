package nosql.mongo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import interfaces.MFMusic;
import utils.IdMusicScore;
import utils.MathUtils;
import utils.TimeInMilliSeconds;

public class MongoServiceSearchUser {
	
	private static MongoService ms = MongoService.getInstance();
	
	static List<MFMusic> getTopMusicSearchByPeriod(TimeInMilliSeconds timeInMilliSeconds){
		List<IdMusicScore> idMusicScore = new ArrayList<>();
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.SEARCH);
		MongoCursor<Document> cursor = ms.findAll(collection);

		boolean presentInList=false;
		String idMusic="";
		long timeOfSearch;
		Date utilDate = new Date();
		
		while(cursor.hasNext()){
			idMusic=cursor.next().getString("idMusic");
			timeOfSearch=cursor.next().getLong("dateSearch");
			if(!((utilDate.getTime()-timeOfSearch) > timeInMilliSeconds.value)){
				for(IdMusicScore musicScore : idMusicScore){
					if(musicScore.getIdMusic().equals(idMusic)){
						musicScore.incrementScore();
						presentInList=true;
						break;
					} 
				}
				if(!presentInList){
					idMusicScore.add(new IdMusicScore(idMusic, 1));
				}
			}
		}
		Collections.sort(idMusicScore);

		MFMusic msDTO;
		List<MFMusic> listMusic= new ArrayList<MFMusic>(idMusicScore.size());
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		MongoCursor<Document> cursor_Musics;
		Document doc_Musics, findQuery_MusicByIdMusic;
		
		for(IdMusicScore ims : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				
				msDTO = MongoUtils.transformDocumentIntoMFMusic(doc_Musics);
				if(msDTO != null) listMusic.add(msDTO);
				
			}
		}
		
		return listMusic;
	}
	
	static void addNewSearch(String idMusic, Date userBirth){
		System.out.println(userBirth);
	    int age = MathUtils.calculAge(userBirth);
	    System.out.println(age);
	    Document doc;
	    GregorianCalendar gc = new GregorianCalendar();
	    String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
		MongoCollection<Document> collection = ms.getCollection(week); 
		
		
		doc = new Document("idMusic", new Document("$eq",idMusic));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		if(cursor.hasNext()){
			Document doc1 = cursor.next();
			int score;
			String tranche ="";
			if(age<18){
				score =  doc1.getInteger("<18");
				tranche="<18";
			}else if(age<24){
				System.out.println("age<24");
				score =  doc1.getInteger("18<=x<24");
				tranche="18<=x<24";
			}else if(age<35){
				tranche="24<=x<35";
				score =  doc1.getInteger("24<=x<35");
			}else if(age<50){
				tranche="35<=x<50";
				score =  doc1.getInteger("35<=x<50");
			}else if(age<65){
				tranche="50<=x<65";
				score =  doc1.getInteger("50<=x<65");
			}else{
				tranche=">=65";
				score = doc1.getInteger(">=65");
			}
			System.out.println("tranche ="+tranche);

			System.out.println("score ="+score);
			score =score+1;
			Document doc2 = new Document(new Document("$set",new Document(tranche,score)));
			ms.updateOne(collection, doc1,doc2);
		}else{
			doc = new Document();
			doc.put("idMusic", idMusic);
			if(age<18){
				doc.put("<18",1);
				doc.put("18<=x<24", 0);
				doc.put("24<=x<35", 0);
				doc.put("35<=x<50", 0);
				doc.put("50<=x<65", 0);
				doc.put(">=65", 0);
			}else if(age<24){
				doc.put("<18",0);
				doc.put("18<=x<24", 1);
				doc.put("24<=x<35", 0);
				doc.put("35<=x<50", 0);
				doc.put("50<=x<65", 0);
				doc.put(">=65", 0);
			}else if(age<35){
				doc.put("<18",0);
				doc.put("18<=x<24", 0);
				doc.put("24<=x<35", 1);
				doc.put("35<=x<50", 0);
				doc.put("50<=x<65", 0);
				doc.put(">=65", 0);
			}else if(age<50){
				doc.put("<18",0);
				doc.put("18<=x<24", 0);
				doc.put("24<=x<35", 0);
				doc.put("35<=x<50", 1);
				doc.put("50<=x<65", 0);
				doc.put(">=65", 0);
			}else if(age<65){
				doc.put("<18",0);
				doc.put("18<=x<24", 0);
				doc.put("24<=x<35", 0);
				doc.put("35<=x<50", 0);
				doc.put("50<=x<65", 1);
				doc.put(">=65", 0);
			}else{
				doc.put("<18",0);
				doc.put("18<=x<24", 0);
				doc.put("24<=x<35", 0);
				doc.put("35<=x<50", 0);
				doc.put("50<=x<65", 0);
				doc.put(">=65", 1);
			}
			ms.insertOne(collection, doc);
		}
	}

	
	public static void generateAndInsertTopOfThisWeek(){
	    List<IdMusicScore> listIdMusicScore = new ArrayList<>();
		GregorianCalendar gc = new GregorianCalendar();
	    String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
	    MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.STAT); 
	    MongoCursor<Document> cursor = ms.findAll(collection);
	    Document doc;
	    IdMusicScore idMusicScore;
	    int score =0;
	    while(cursor.hasNext()){
	    	doc = cursor.next();
	    	score += doc.getInteger("<18")+doc.getInteger("18<=x<24")+doc.getInteger("24<=x<35")+
	    			doc.getInteger("35<=x<50")+doc.getInteger("50<=x<65")+doc.getInteger(">=65");
	    	idMusicScore=new IdMusicScore(doc.getString("idMusic"), score);
	    	listIdMusicScore.add(idMusicScore);
	    	score=0;
	    }
	    Collections.sort(listIdMusicScore);

	    collection = ms.getCollection("TOP-Week-"+week); 
	    MongoCursor<Document> cursor_Musics;
		Document doc_Musics, findQuery_MusicByIdMusic;
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
	    for(IdMusicScore ims : listIdMusicScore){
	    	findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				doc = new Document();
				doc.put("idMusic", ims.getIdMusic());
				doc.put("nameMusic", doc_Musics.getString("nameMusic"));
				doc.put("artistName", doc_Musics.getString("artistName"));
				doc.put("spotifyId", doc_Musics.getString("spotifyId"));
				doc.put("soundCloudId", doc_Musics.getString("soundCloudId"));
				ms.insertOne(collection, doc);
			}
	    }
	}
	
	public static void generateAndInsertTopOfThisWeekByAge(String intervalle){
	    List<IdMusicScore> listIdMusicScore = new ArrayList<>();
	    int score =0;
		GregorianCalendar gc = new GregorianCalendar();
	    String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
	    
	    MongoCollection<Document> collection = ms.getCollection(week); 
	    MongoCursor<Document> cursor = ms.findAll(collection);
	    Document doc;
	    while(cursor.hasNext()){
	    	doc = cursor.next();
	    	switch(intervalle){
	    	case "<18" :score += doc.getInteger("<18");
	    				break;
	    	case "18<=x<24" :score += doc.getInteger("18<=x<24");
	    					break;
	    	case "24<=x<35" :score += doc.getInteger("18<=x<24");
							break;
	    	case "35<=x<50" :score += doc.getInteger("35<=x<50");
	    					break;
	    	case "50<=x<65" :score += doc.getInteger("50<=x<65");
	    					break;
	    	case ">=65"		:score += doc.getInteger(">=65");
	    					break;
	    	}
	    	listIdMusicScore.add(new IdMusicScore(doc.getString("idMusic"), score));
	    	score=0;
	    }
	    Collections.sort(listIdMusicScore);

	    collection = ms.getCollection("TOP-Week-"+week+"-"+intervalle); 
	    MongoCursor<Document> cursor_Musics;
		Document doc_Musics, findQuery_MusicByIdMusic;
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
	    for(IdMusicScore ims : listIdMusicScore){
	    	findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ 
				doc_Musics = cursor_Musics.next();
				doc = new Document();
				doc.put("idMusic", ims.getIdMusic());
				doc.put("nameMusic", doc_Musics.getString("nameMusic"));
				doc.put("artistName", doc_Musics.getString("artistName"));
				doc.put("spotifyId", doc_Musics.getString("spotifyId"));
				doc.put("soundCloudId", doc_Musics.getString("soundCloudId"));
				ms.insertOne(collection, doc);
			}
	    }
	}

	public static void deleteCacheUserExceedOneHour(){
		long timeNow = new Date().getTime() - TimeInMilliSeconds.HOUR.value;
		Document doc;
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.CACHE);
		doc = new Document("time", new Document("$lt",timeNow));
		System.out.println(timeNow);
		System.out.println(doc);
		ms.deleteMany(collection, doc);
	}

}