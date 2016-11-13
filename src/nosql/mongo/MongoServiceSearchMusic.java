package nosql.mongo;

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
	
	/**
	 * Méthode permettant de filtrer une liste de musiques pour retourner une liste de musiques sans doublon
	 * On ne prend qu'une musique par album la ou les autres musiques seront également récupérées lors de l'insertion
	 * @param musics liste des musiques que l'on souhaite triée
	 * @return la liste des musiques triées
	 */
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

	/**
	 * Méthode principale de recherche qui retourne la liste des musiques répondant aux mieux au critère de recherche
	 * Si il y a trop de musiques répondant aux critères une partie des résultats sera stocké dans une base Mongo
	 * @param tags liste des mots recherché par l'utilisateur 
	 * @param idRecherche identifiant unique de la recherche
	 * @return la liste des musiques répondant aux critère au maximum 
	 */
	static List<MFMusic> searchMusics(List<String> tags, String idRecherche){
		
		List<MFMusic> resultSearchMusicsByTagsInLyrics = ms.searchMusicsByTagsInLyrics(tags, idRecherche);
		if(resultSearchMusicsByTagsInLyrics.isEmpty()){
			List<MFMusic> resultInTagsByTags = ms.searchMusicsByTagsInTags(tags, idRecherche);
			return resultInTagsByTags;
		}
		return resultSearchMusicsByTagsInLyrics;
	}
	
	/**
	 * Méthode faisant une recherche dans la collection MongoCollectionsAndKeys.TAGS pour chaque mot de la liste
	 * passée en paramètre, chaque mot est indépendant de l'autre
	 * Si il y a trop de musiques répondant aux critères une partie des résultats sera stocké dans une base Mongo
	 * @param tags liste des mots recherché
	 * @param idRecherche identifiant unique de la recherche
	 * @return La liste des musiques correspondant aux mieux à la liste entrée en paramètre
	 */
	static List<MFMusic> searchMusicsByTagsInTags(List<String> tags, String idRecherche){
		List<IdMusicScore> listIdMusicScore = new ArrayList<>();
		List<String> listIdMusics = new ArrayList<>();
		List<List<Document>> listDocsTags=new ArrayList<>();
		List<Document> listDoc=new ArrayList<>();
		boolean presentInList = false;
		String idMusic;
		int nbOccur;
		for(String tag : tags){
			//TODO: Voir si il faut pas lancer l'appel en passant par MogoService
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
		//TODO: Voir si il faut pas lancer l'appel en passant par MogoService
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
			MongoCursor<Document> cursor_Music = ms.findBy(collection_Musics, docRegex);
			
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

	/**
	 * Méthode faisant une recherche dans la collection MongoCollectionsAndKeys.MUSICS en considérant que les mots
	 * présent dans la liste passée en paramètre soit liés.
	 * Si il y a trop de musiques répondant aux critères une partie des résultats sera stocké dans une base Mongo
	 * @param tags liste des mots recherché
	 * @param idRecherche identifiant unique de la recherche
	 * @return La liste des musiques correspondant aux mieux à la liste entrée en paramètre
	 */
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

	/**
	 * Méthode permettant de générer une liste de MFMusic à partir d'une liste d'identifiant de musique passé en paramètre
	 * Si il y a trop de musiques passé en paramètre (Plus de MuzifFinderPreferences.LIMITACCEPTABLETEMPS) les 
	 * MuzifFinderPreferences.LIMITACCEPTABLETEMPS sont rétournés dans une liste au format MFMusic et le reste
	 * des identifiants de musiques dans une collection Mongo avec les tags associés et l'identifiant de la recherche
	 * @param tags liste des mots passés en paramétre de la recherche amenant à l'appel de cette méthode
	 * @param idMusics liste des musiques a tansformer au format MFMusic
	 * @param idRecherche identifiant de la recherche amenant à l'appel de cette méthode
	 * @return la liste des musiques aux formats MFMusic
	 */
	static List<MFMusic> generateListMFMusicWithListId(List<String> tags, List<String> idMusics, String idRecherche){
		List<String> copyIdMusics= new ArrayList<String>();
		List<MFMusic> listMDTO = new ArrayList<MFMusic>();
		if(!idMusics.isEmpty()){
			copyIdMusics.addAll(idMusics);
			MFMusic msDTO;
			MongoCollection<Document> collection_Musics;
			MongoCursor<Document> cursor_Musics;
			Document doc_Musics, findQuery_MusicByIdMusic;
			
			for(int i=0; i<idMusics.size() && i < MuzikFinderPreferences.LIMITACCEPTABLETEMPS; i++){
				
				collection_Musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
				findQuery_MusicByIdMusic = new Document(MongoCollectionsAndKeys.IDMUSIC_MUSICS, new Document("$eq", idMusics.get(i)));
				cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);
				if(cursor_Musics.hasNext()){  
					doc_Musics = cursor_Musics.next(); 
					msDTO = MongoUtils.transformDocumentIntoMFMusic(doc_Musics);
					
					if(msDTO != null) listMDTO.add(msDTO);
				}
			}
			copyIdMusics=copyIdMusics.subList(listMDTO.size(), copyIdMusics.size());
		}
		if(copyIdMusics != null){
			ms.insertCacheSearchUser(tags, copyIdMusics, idRecherche);
		}
		return listMDTO;
	}
	
	/**
	 * Méthode retournant les musiques pas encore récupérées pour la recherche ayant comme identifiant de recherche
	 * l'identifiant passé en paramètre
	 * Si il y a trop de musiques a affiché les MuzifFinderPreferences.LIMITACCEPTABLETEMPS sont affichés, les
	 * autres reste stocké dans une collection Mongo
	 * @param idRecherche identifiant de la recherche
	 * @return la liste des musiques aux formats MFMusic
	 */
	@SuppressWarnings("unchecked")
	static List<MFMusic> getMoreResults(String idRecherche){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.SEARCH_CACHE);
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

		//TODO: Voir si il faut pas lancer l'appel en passant par MogoService
		return generateListMFMusicWithListId(tags, listIdMusic, idRecherche);
	}

	/**
	 * Méthode retournant un document contenant un regex compréhensible par mongo pour lui signifier
	 * que les mots passés en paramètre doivent être traités comme une phrase
	 * @param listTags liste des tags à partir desquels le regex doit être formé
	 * @return le document compréhensible par mongo comprenant le regex
	 */
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

	/**
	 * Méthode retournant l'ensemble des musiques d'un artiste à partir de son nom
	 * @param artistName nom de l'artiste
	 * @return liste des MFMusic d'un artiste
	 */
	static List<MFMusic> getMusicsByArtist(String artistName){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		/*String regex ="[,.\\n ]?[,.\\n ]?";
		artistName+=regex;*/
		Document findQuery = new Document(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS, new Document("$regex",artistName).append("$options","i"));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		
		List<MFMusic> listMusic= new ArrayList<>();
		MFMusic music = null;
		while(cursor.hasNext()){
			music = MongoUtils.transformDocumentIntoMFMusic(cursor.next());
			listMusic.add(music);
		}
		return listMusic;
	}
	
	/**
	 * Méthode retournant l'ensemble des musiques d'un album à partir de son nom
	 * @param trackName nom de l'album
	 * @return liste des MFMusic de l'album
	 */
	static List<MFMusic> getMusicsByTrackName(String trackName){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document findQuery = new Document(MongoCollectionsAndKeys.MUSICNAME_MUSICS, new Document("$eq",trackName));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		
		List<MFMusic> listMusic= new ArrayList<>();
		MFMusic music = null;
		while(cursor.hasNext()){
			music = MongoUtils.transformDocumentIntoMFMusic(cursor.next());
			listMusic.add(music);
		}
		return listMusic;
	}
}
