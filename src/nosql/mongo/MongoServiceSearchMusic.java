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
import utils.IdMusicScore;
import utils.MuzikFinderPreferences;

public class MongoServiceSearchMusic {
	
	private static MongoService ms = MongoService.getInstance();
	
	static List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
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

	//TODO : retirer les Instant en fin de tests
	static List<MFMusic> searchMusics(List<String> tags, String idRecherche){
		
		Instant start = Instant.now();
		List<MFMusic> resultSearchMusicsByTagsInLyrics = ms.searchMusicsByTagsInLyrics(tags, idRecherche);
		Instant end = Instant.now();
		System.out.println("searchMusicsByTagsInLyrics =="+Duration.between(start, end));
		System.out.println("resultSearchMusicsByTagsInLyrics size =="+resultSearchMusicsByTagsInLyrics.size());
		if(resultSearchMusicsByTagsInLyrics.isEmpty()){
			start = Instant.now();
			List<MFMusic> resultInTagsByTags = ms.searchMusicsByTagsInTags(tags, idRecherche);
			end = Instant.now();
			System.out.println("searchMusicByTagsInTags =="+Duration.between(start, end)); // prints PT1M3.553S
			System.out.println("resultInTagsByTags size = "+resultInTagsByTags.size());
			return resultInTagsByTags;
		}
		return resultSearchMusicsByTagsInLyrics;
	}
	
	static List<MFMusic> searchMusicsByTagsInTags(List<String> tags, String idRecherche){
		List<IdMusicScore> listIdMusicScore = new ArrayList<>();
		List<String> listIdMusics = new ArrayList<>();
		boolean presentInList = false;
		String idMusic;
		List<List<Document>> listDocsTags=new ArrayList<>();
		List<Document> listDoc=new ArrayList<>();
		int nbOccur;
		for(String tag : tags){
			listDoc=MongoServiceGetId.getListDocumentIdMusicScoreByTag(tag);
			listDocsTags.add(listDoc);
			for(Document doc : listDoc){
				idMusic=doc.getString(MongoCollectionsAndKeys.MUSICID_TAGS);
				nbOccur=doc.getInteger(MongoCollectionsAndKeys.SCORE_TAGS);
				for(IdMusicScore musicScore : listIdMusicScore){
					if(musicScore.getIdMusic().equals(idMusic)){
						musicScore.incrementScore();
						musicScore.setNbOccur(musicScore.getNbOccur()+nbOccur);
						presentInList=true;
						break;
					} 
				}
				if(!presentInList){
					IdMusicScore ims = new IdMusicScore(idMusic, 1, nbOccur);
					listIdMusicScore.add(ims);
				}else{
					presentInList=false;
				}
			}
		}
		
		listIdMusics.clear();
		Collections.sort(listIdMusicScore);
		for(IdMusicScore iMS: listIdMusicScore){
			listIdMusics.add(iMS.getIdMusic());
		}
		return generateListMFMusicWithListId(tags, listIdMusics, idRecherche);
	}
	
	/**
	 * Cette méthode permet de chercher les musics correspondantes au tags entrés par
	 * l'utilisateur en traitant les tags comme une phrase complète
	 * @param tags
	 * @return
	 */
	static List<String> matchMusicsWithTags(List<String> tags){
		Document docRegex = formRegexForSearch(tags);
		if(docRegex!=null){
			ArrayList<String> result = new ArrayList<String>();
			MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
			MongoCursor<Document> cursor_Music = ms.findBy(collection_Musics, formRegexForSearch(tags));
			
			// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
			Document doc_lyrics;
			String idMusic;
			while(cursor_Music.hasNext()){
				doc_lyrics = cursor_Music.next();
				idMusic = doc_lyrics.getString(MongoCollectionsAndKeys.IDMUSIC_MUSICS);
				result.add(idMusic);
			}
			return result;
		}
		return null;
	}

	static List<MFMusic> searchMusicsByTagsInLyrics(List<String> tags, String idRecherche){
		List<MFMusic> listMDTO = new ArrayList<MFMusic>();
		if(tags.size() < MuzikFinderPreferences.MIN_SIZE_OF_TAGS_FOR_SEARCH){	//Si il y a moins de 3 tags on ne fait pas de recherche by tags in lyrics
			return listMDTO;
		}
		int i=0;
		int k=0;
		List<String> tag = new ArrayList<String>();
		List<String> tmp = new ArrayList<String>();
		while(tags.size()-i >= MuzikFinderPreferences.MIN_SIZE_OF_TAGS_FOR_SEARCH){
			k=0;
			while(k <= i){
				for(int j=k; j<tags.size()-i+k; j++){
					tag.add(tags.get(j));
				}
				tmp.addAll(ms.matchMusicsWithTags(tag));
				tag.removeAll(tag);
				k++;
			}
			if(!tmp.isEmpty()){
				break;
			}
			i++;
		}
	
		return generateListMFMusicWithListId(tags, tmp, idRecherche);
	}

	//TODO : le nom de ta fonction à l'air de dire qu'il va juste transformer des Musics en MFMusic, sauf qu'il insère aussi dans cache
	// donc peut-etre à sortir dans une autre fonction. C'est normalement pas son rôle. Sortir la partie cache dans une autre fonction
	static List<MFMusic> generateListMFMusicWithListId(List<String> tags, List<String> idMusics, String idRecherche){
		Instant start = Instant.now();
		List<String> copyIdMusics= new ArrayList<String>();
		List<MFMusic> listMDTO = new ArrayList<MFMusic>();
		if(!idMusics.isEmpty()){
			copyIdMusics.addAll(idMusics);
			Instant begin = Instant.now();
			for(int i=0; i<idMusics.size() && i < MuzikFinderPreferences.LIMITACCEPTABLETEMPS; i++){
				MFMusic msDTO;
				MongoCollection<Document> collection_Musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
				
				MongoCursor<Document> cursor_Musics;
				Document doc_Musics, findQuery_MusicByIdMusic;
				findQuery_MusicByIdMusic = new Document(MongoCollectionsAndKeys.IDMUSIC_MUSICS, new Document("$eq", idMusics.get(i)));
				cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
				if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
					doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
					msDTO = MongoUtils.transformDocumentIntoMFMusic(doc_Musics);
					
					if(msDTO != null) listMDTO.add(msDTO);
				}
			}
			Instant finish = Instant.now();
			System.out.println("boucle for time =="+Duration.between(begin, finish));
			copyIdMusics=copyIdMusics.subList(listMDTO.size(), copyIdMusics.size());
		}
		if(copyIdMusics != null){
			ms.insertCacheSearchUser(tags, copyIdMusics, idRecherche);
		}
		Instant end=Instant.now();
		System.out.println("generateListMFMusicWithListId time =="+Duration.between(start, end));
		return listMDTO;
	}
	
	@SuppressWarnings("unchecked")
	static List<MFMusic> getMoreResults(String idRecherche){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.CACHE); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document(MongoCollectionsAndKeys.SEARCHID_CACHE, new Document("$eq",idRecherche));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		
		List<String> listIdMusic = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		if(cursor.hasNext()){
			Document doc = cursor.next();
			listIdMusic = (ArrayList<String>) doc.get(MongoCollectionsAndKeys.MUSICSID_CACHE);
			tags = (ArrayList<String>) doc.get(MongoCollectionsAndKeys.TAGS_CACHE);
		}
		if(listIdMusic ==null || listIdMusic.isEmpty()) return ms.searchMusicsByTagsInTags(tags, idRecherche);
		return generateListMFMusicWithListId(tags, listIdMusic, idRecherche);
	}
	
	static List<IdMusicScore> sortByScoreInTag(List<List<Document>> listDocuments, List<IdMusicScore> listIdMusicScore){
		String idMusic;
		int score;
		for(List<Document> listDoc: listDocuments){
			for(Document doc: listDoc){
				idMusic=doc.getString(MongoCollectionsAndKeys.MUSICID_TAGS);
				score=doc.getInteger(MongoCollectionsAndKeys.SCORE_TAGS);
				for(IdMusicScore ims : listIdMusicScore){
					if(idMusic.equals(ims.getIdMusic())){
						ims.setScore(ims.getScore()+score);
						break;
					}
				}
			}
		}
		Collections.sort(listIdMusicScore);

		return listIdMusicScore;
	}
	
	private static Document formRegexForSearch(List<String> listTags){
		String regex ="[,.\\n ]?[,.\\n ]?";
		String search="";
		if(listTags!=null){
			if(listTags.size()==1){
				return new Document(MongoCollectionsAndKeys.LYRICS_MUSICS,new Document("$regex",listTags.get(0)).append("$options", "i"));
			}
			for(String tag : listTags){
				search+=tag+regex;
			}
			return new Document(MongoCollectionsAndKeys.LYRICS_MUSICS,new Document("$regex",search).append("$options", "i"));
		}
		return null;
	}

	
	public static void main(String[] args){
		List<String> tags = new ArrayList<>();
		tags.add("i");
		searchMusics(tags, "llpkpkp");
		System.out.println("FIN PRECHAUFFE");
		tags.clear();
		tags.add("red");
		tags.add("light");
		tags.add("trump");
		tags.add("black");
		tags.add("nigga");
		Instant start = Instant.now();
		searchMusics(tags, "idRecherche999");
		Instant end = Instant.now();
		System.out.println("searchMusicByTagsInTags =="+Duration.between(start, end));
	}
}
