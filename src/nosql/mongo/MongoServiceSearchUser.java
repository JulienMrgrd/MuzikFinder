package nosql.mongo;

import java.util.Date;
import java.util.ArrayList;
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
	
	public static void addNewSearch(String idMusic, Date userBirth, MongoService ms){
		Date utilDate = new Date();
	    int age = MathUtils.calculAge(userBirth);

		MongoCollection<Document> collection = ms.getCollection(MongoCollections.SEARCH); 
		Document doc = new Document();
		doc.put("idMusic", idMusic);
		doc.put("userAge",age);
		doc.put("dateSearch", utilDate.getTime());
		ms.insertOne(collection, doc);
	}
	
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
	
}