package nosql.mongo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import interfaces.MFMusic;
import server.dto.MusicDTO;
import utils.IdMusicScore;
import utils.MuzikFinderPreferences;

public class MongoServiceSearchMusic {
	
	public static List<MFMusic> filterByExistingMusics(List<MFMusic> musics, MongoService ms) {
		List<String> idAlbumAlreadyExist = ms.getAllAlbumIds();

		if(idAlbumAlreadyExist==null) return musics;

		List<MFMusic> listReduce = new ArrayList<MFMusic>();
		for(MFMusic mfm : musics){
			if(!idAlbumAlreadyExist.contains(mfm.getAlbumId())){
				listReduce.add(mfm);
			}
		}
		return listReduce;
	}

	public static List<MusicDTO> searchMusics(List<String> tags, MongoService ms){
		List<MusicDTO> result = new ArrayList<MusicDTO>();
		List<MusicDTO> result_tmp = new ArrayList<MusicDTO>();
		Instant start = Instant.now();
		List<MusicDTO> resultInTagsByTags_tmp = ms.searchMusicsByTagsInTags(tags);
		Instant end = Instant.now();
		System.out.println("searchMusicByTagsInTags =="+Duration.between(start, end)); // prints PT1M3.553S
		
		List<MusicDTO> resultInLyricsByTags_tmp;

		System.out.println(resultInTagsByTags_tmp.size());

		if(resultInTagsByTags_tmp.size()>MuzikFinderPreferences.getPrefNbMusicFilter()[0]){
			//result_tmp.addAll(resultInTagsByTags_tmp);
			start = Instant.now();
			resultInLyricsByTags_tmp = ms.searchMusicsByTagsInLyrics(tags);
			end = Instant.now();
			System.out.println("searchMusicsByTagsInLyrics == "+Duration.between(start, end)); // prints PT1M3.553S
			for(MusicDTO res : resultInTagsByTags_tmp)
				if(resultInLyricsByTags_tmp.contains(res))
					result_tmp.add(res);
	
			//TODO: A verifier si y faut tout supprimer ou pas
			/*	resultInLyricsByTagsLikeLyrics_tmp = searchMusicsByTagsWithLyrics(tags);
			if(resultInLyricsByTagsLikeLyrics_tmp.size()>MuzikFinderPreferences.getPrefNbMusicFilter()[1]){
				for(MusicDTO res : resultInLyricsByTagsLikeLyrics_tmp)
					if(result_tmp.contains(res))
						result.add(res);
			}
			else{
				result.addAll(result_tmp);
			}*/
		}else{
			result.addAll(resultInTagsByTags_tmp);
		}
		//result.addAll(result_tmp);
		return result;
	}
	
	public static List<MusicDTO> searchMusicsByTagsInTags(List<String> tags, MongoService ms){
		List<IdMusicScore> idMusicScore = new ArrayList<>();
		List<String> listIdMusics = new ArrayList<>();
		boolean presentInList = false;

		Instant start = Instant.now();
		for(String tag : tags){
			listIdMusics = ms.getIdMusicsByTag(tag);
			for(String idMusic : listIdMusics){
				for(IdMusicScore musicScore : idMusicScore){
					if(musicScore.getIdMusic().equals(idMusic)){
						musicScore.incrementScore();
						presentInList=true;
						break;
					} 
				}
				if(!presentInList){
					IdMusicScore ims = new IdMusicScore(idMusic, 1);
					idMusicScore.add(ims);
				}else{
					presentInList=false;
				}
			}
		}
		Instant end = Instant.now();
		
		System.out.println("1er For searchMusicsByTagsInTags =="+Duration.between(start, end));
		
		
		Collections.sort(idMusicScore);
		MusicDTO msDto;
		List<MusicDTO> listMusic= new ArrayList<MusicDTO>(idMusicScore.size());

		System.out.println("size searchMusicsByTagsInTags== "+idMusicScore.size());
		
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = ms.getCollection(MongoCollections.ARTISTS);
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		MongoCursor<Document> cursor_Musics, cursor_Artists;
		Document doc_Musics, doc_Artists, findQuery_Artists, findQuery_MusicByIdMusic;
		int i=0;

		start = Instant.now();
		for(IdMusicScore ims : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				String nameArtist = "";
				//On recupere l'identifiant de l'artiste ayant fait la musique
				/*findQuery_Artists = new Document("idArtist", new Document("$eq",doc_Musics.getString("idArtist")));
				cursor_Artists = findBy(collection_Artists,findQuery_Artists);
				
				
				if(cursor_Artists.hasNext()){
					doc_Artists = cursor_Artists.next();
					nameArtist=doc_Artists.getString("nameArtist");
				}*/
				msDto = new MusicDTO(ims.getIdMusic(), doc_Musics.getString("nameMusic"), doc_Musics.getString("idArtist"),
						nameArtist, "", doc_Musics.getString("spotifyId"), doc_Musics.getString("soundcloudId"));
				listMusic.add(msDto);
				
			}
			i++;
			//if(i>10) break;
		}
		end = Instant.now();
		System.out.println(" 2eme For searchMusicsByTagsInTags=="+Duration.between(start, end));
		return listMusic;
	}
	

	/**
	 * Cette méthode permet de chercher les musics correspondantes au tags entrés par
	 * l'utilisateur en traitant les tags comme une phrase complète
	 * @param tags
	 * @return
	 */
	public static List<String> matchMusicsWithTags(List<String> tags, MongoService ms){
		ArrayList<String> result = new ArrayList<String>();
		String words = tags.toString();
		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollections.MUSICS);
		//new Document("$regex",search)
		MongoCursor<Document> cursor_Music = ms.findBy(collection_Musics, ms.formRegexForSearch(tags));
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		Document doc_lyrics;
		String lyrics;
		while(cursor_Music.hasNext()){
			doc_lyrics = cursor_Music.next();
			lyrics = doc_lyrics.getString("lyrics");
			if(lyrics.contains(words)) result.add(doc_lyrics.getString("nameMusic"));
		}
		return result;
	}

	public static List<MusicDTO> searchMusicsByTagsInLyrics(List<String> tags, MongoService ms){
		List<MusicDTO> listMusics = new ArrayList<MusicDTO>();
		List<String> list_NameMusics = ms.matchMusicsWithTags(tags);
		
		MusicDTO mDto;

		MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = ms.getCollection(MongoCollections.ARTISTS);
		MongoCursor<Document> cursor_Music; 
		MongoCursor<Document> cursor_Artist; 
		
		Document findQuery_Musics;
		Document doc_Music;
		String idArtist;
		Document findQuery_Artists;
		Document doc_Artist;

		for(String nameMusic : list_NameMusics){
			findQuery_Musics = new Document("nameMusic", new Document("$eq",nameMusic));			
			cursor_Music = ms.findBy(collection_Musics,findQuery_Musics);
			doc_Music = cursor_Music.next();
			idArtist = doc_Music.getString("idArtist");
			findQuery_Artists = new Document("idArtist", new Document("$eq",idArtist));
			cursor_Artist = ms.findBy(collection_Artists,findQuery_Artists);
			//if(cursor_Artist.hasNext()){
				doc_Artist = cursor_Artist.next();
				mDto = new MusicDTO(doc_Music.getString("idMusic"), nameMusic, idArtist, doc_Artist.getString("nameArtist"),
						"", //albumId
						doc_Music.getString("spotifyId"), doc_Music.getString("soundcloudId"));
	
				listMusics.add(mDto);
			//}
		}

		return listMusics;
	}


}
