package nosql.mongo;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import server.dto.MusicDTO;
import utils.IdMusicScore;
import utils.MathUtils;
import utils.TimeInMilliSeconds;

public class MongoServiceSearchUser {
	
	public static List<MusicDTO> getTopMusicSearchByPeriod(MongoService ms, TimeInMilliSeconds timeInMilliSeconds){
		List<IdMusicScore> idMusicScore = new ArrayList<>();
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.SEARCH);
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

		MusicDTO msDto;
		List<MusicDTO> listMusic= new ArrayList<MusicDTO>(idMusicScore.size());
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollections.MUSICS);
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		MongoCursor<Document> cursor_Musics;
		Document doc_Musics, findQuery_MusicByIdMusic;
		
		for(IdMusicScore ims : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				
				msDto = new MusicDTO(ims.getIdMusic(), doc_Musics.getString("nameMusic"), doc_Musics.getString("idArtist"),
						doc_Musics.getString("artistName"), "", doc_Musics.getString("spotifyId"), doc_Musics.getString("soundcloudId"));
				listMusic.add(msDto);
				
			}
		}
		
		return listMusic;
	}
	
	public static void addNewSearch(String idMusic, Date userBirth, MongoService ms){
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
	
}