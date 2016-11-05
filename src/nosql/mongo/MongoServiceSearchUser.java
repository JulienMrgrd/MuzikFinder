package nosql.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import server.dto.MusicDTO;
import sql.User;
import utils.IdMusicScore;
import utils.MathUtils;
import utils.TimeInMilliSeconds;

public class MongoServiceSearchUser {
	
	public static void addNewSearch(String idMusic, User user, MongoService ms){
		java.util.Date utilDate = new java.util.Date();
		
	    int age = MathUtils.calculAge(user.getDateBirth());

		MongoCollection<Document> collection = ms.getCollection(MongoCollections.SEARCH); 
		Document doc = new Document();
		doc.put("idMusic", idMusic);
		doc.put("ageUser",age);
		doc.put("dateSearch", utilDate.getTime());
		ms.insertOne(collection, doc);
		
	}
	
	public static List<MusicDTO> getTopMusicSearchThisWeek(MongoService ms){
		List<IdMusicScore> idMusicScore = new ArrayList<>();
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.SEARCH);
		MongoCursor<Document> cursor = ms.findAll(collection);

		boolean presentInList=false;
		String idMusic="";
		long timeOfSearch;

		java.util.Date utilDate = new java.util.Date();
		
		while(cursor.hasNext()){
			idMusic=cursor.next().getString("idMusic");
			timeOfSearch=cursor.next().getLong("dateSearch");
			if(!((utilDate.getTime()-timeOfSearch)>TimeInMilliSeconds.WEEK)){
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

		System.out.println(listMusic.size());
		
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = ms.getCollection(MongoCollections.ARTISTS);
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		MongoCursor<Document> cursor_Musics, cursor_Artists;
		Document doc_Musics, doc_Artists, findQuery_Artists, findQuery_MusicByIdMusic;
		
		for(IdMusicScore ims : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				
				//On recupere l'identifiant de l'artiste ayant fait la musique
				findQuery_Artists = new Document("idArtist", new Document("$eq",doc_Musics.getString("idArtist")));
				cursor_Artists = ms.findBy(collection_Artists,findQuery_Artists);
				String nameArtist = "";
				
				if(cursor_Artists.hasNext()){
					doc_Artists = cursor_Artists.next();
					nameArtist=doc_Artists.getString("nameArtist");
				}
				msDto = new MusicDTO(ims.getIdMusic(), doc_Musics.getString("nameMusic"), doc_Musics.getString("idArtist"),
						nameArtist, "", doc_Musics.getString("spotifyId"), doc_Musics.getString("soundcloudId"));
				listMusic.add(msDto);
				
			}
		}
		
		return listMusic;
	}
	
	public static List<MusicDTO> getTopMusicSearchThisMonth(MongoService ms){
		List<IdMusicScore> idMusicScore = new ArrayList<>();
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.SEARCH);
		MongoCursor<Document> cursor = ms.findAll(collection);

		boolean presentInList=false;
		String idMusic="";
		long timeOfSearch;

		java.util.Date utilDate = new java.util.Date();
		
		while(cursor.hasNext()){
			idMusic=cursor.next().getString("idMusic");
			timeOfSearch=cursor.next().getLong("dateSearch");
			if(!((utilDate.getTime()-timeOfSearch)>TimeInMilliSeconds.MONTH)){
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

		System.out.println(listMusic.size());
		
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = ms.getCollection(MongoCollections.ARTISTS);
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		MongoCursor<Document> cursor_Musics, cursor_Artists;
		Document doc_Musics, doc_Artists, findQuery_Artists, findQuery_MusicByIdMusic;
		
		for(IdMusicScore ims : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				
				//On recupere l'identifiant de l'artiste ayant fait la musique
				findQuery_Artists = new Document("idArtist", new Document("$eq",doc_Musics.getString("idArtist")));
				cursor_Artists = ms.findBy(collection_Artists,findQuery_Artists);
				String nameArtist = "";
				
				if(cursor_Artists.hasNext()){
					doc_Artists = cursor_Artists.next();
					nameArtist=doc_Artists.getString("nameArtist");
				}
				msDto = new MusicDTO(ims.getIdMusic(), doc_Musics.getString("nameMusic"), doc_Musics.getString("idArtist"),
						nameArtist, "", doc_Musics.getString("spotifyId"), doc_Musics.getString("soundcloudId"));
				listMusic.add(msDto);
				
			}
		}
		
		return listMusic;
	}
	
}
