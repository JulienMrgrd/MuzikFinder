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
import server.dto.MFMusicDTO;
import utils.IdMusicScore;
import utils.MathUtils;
import utils.MuzikFinderPreferences;
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

		// On sors les variables temporaires du for pour utiliser efficacement l'espace mÃ©moire
		MongoCursor<Document> cursor_Musics;
		Document doc_Musics, findQuery_MusicByIdMusic;

		for(IdMusicScore ims : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ims.getIdMusic()));
			cursor_Musics = ms.findBy(collection_Musics, findQuery_MusicByIdMusic);

			if(cursor_Musics.hasNext()){ // On rÃ©cupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic

				msDTO = MongoUtils.transformDocumentIntoMFMusic(doc_Musics);
				if(msDTO != null) listMusic.add(msDTO);

			}
		}

		return listMusic;
	}

	@SuppressWarnings("unused")
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


	@SuppressWarnings("unchecked")
	static List<IdMusicScore> getListIdMusicScoreMostPopularByRange(String range){
		System.out.println("Début getListIdMUsicScoreMostPopularByRange : "+range);
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.STATS);
		GregorianCalendar gc = new GregorianCalendar();
		String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
		Document doc = new Document(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS,new Document("$eq",week));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		List<IdMusicScore> list_music_score = new ArrayList<IdMusicScore>();
		if(cursor.hasNext()){
			Document doc_weeks = cursor.next();
			List<Document> list_age_range = (List<Document>) doc_weeks.get(MongoCollectionsAndKeys.AGERANGE_STATS);
			for(Document doc_range : list_age_range){
				if(doc_range.getString(MongoCollectionsAndKeys.AGE_STATS).equals(range)){
					List<Document> doc_musics = (List<Document>) doc_range.get(MongoCollectionsAndKeys.MUSICS_STATS);
					for(Document d : doc_musics){
						IdMusicScore mscore= new IdMusicScore(d.getString(MongoCollectionsAndKeys.IDMUSIC_STATS)
								,d.getInteger(MongoCollectionsAndKeys.SCOREMUSIC_STATS));
						list_music_score.add(mscore);
					}
				}
			}
		}
		Collections.sort(list_music_score);
		System.out.println(list_music_score);
		System.out.println("Fin getListIdMUsicScoreMostPopularByRange : "+range);
		return list_music_score;
	}

	static List<IdMusicScore> getListIdMusicScoreMostPopularAllRange(){
		int test = 0;
		List<IdMusicScore> list_music_score = getListIdMusicScoreMostPopularByRange(MongoCollectionsAndKeys.MINUSEIGHTEEN_STATS);
		List<IdMusicScore> list_music_score_min_twenty_five = getListIdMusicScoreMostPopularByRange(MongoCollectionsAndKeys.MINUSTWENTYFIVE_STATS);
		List<IdMusicScore> list_music_score_min_fifty = getListIdMusicScoreMostPopularByRange(MongoCollectionsAndKeys.MINUSFIFTY_STATS);
		List<IdMusicScore> list_music_score_plus_fifty = getListIdMusicScoreMostPopularByRange(MongoCollectionsAndKeys.PLUSFIFTY_STATS);

		for(IdMusicScore id_score : list_music_score_min_twenty_five){
			for(IdMusicScore id_score_tmp : list_music_score){
				if(id_score.getIdMusic() == id_score_tmp.getIdMusic()){
					test = 1;
					id_score_tmp.setScore(id_score_tmp.getScore()+id_score.getScore());
				}
			}
			if(test==0){
				list_music_score.add(id_score);
			}
			test = 0;
		}
		for(IdMusicScore id_score : list_music_score_min_fifty){
			for(IdMusicScore id_score_tmp : list_music_score){
				if(id_score.getIdMusic() == id_score_tmp.getIdMusic()){
					test = 1;
					id_score_tmp.setScore(id_score_tmp.getScore()+id_score.getScore());
				}
			}
			if(test==0){
				list_music_score.add(id_score);
			}
			test = 0;
		}
		for(IdMusicScore id_score : list_music_score_plus_fifty){
			for(IdMusicScore id_score_tmp : list_music_score){
				if(id_score.getIdMusic() == id_score_tmp.getIdMusic()){
					test = 1;
					id_score_tmp.setScore(id_score_tmp.getScore()+id_score.getScore());
				}
			}
			if(test==0){
				list_music_score.add(id_score);
			}
			test = 0;
		}
		Collections.sort(list_music_score);
		return list_music_score;
	}

	static List<String> getListIdMostPopularAllRangeInStats(String range){
		List<IdMusicScore> list_music_score = getListIdMusicScoreMostPopularAllRange();
		if(list_music_score.size()> MuzikFinderPreferences.LIMITACCEPTABLETEMPS)
			list_music_score.subList(0, MuzikFinderPreferences.LIMITACCEPTABLETEMPS);
		List<String> list_id = new ArrayList<String>();
		for(IdMusicScore mscore : list_music_score){
			list_id.add(mscore.getIdMusic());
		}
		System.out.println(list_id);
		return list_id;

	}

	static List<String> getListIdMostPopularByRangeInStats(String range){
		List<IdMusicScore> list_music_score = getListIdMusicScoreMostPopularByRange(range);
		if(list_music_score.size()> MuzikFinderPreferences.LIMITACCEPTABLETEMPS)
			list_music_score.subList(0, MuzikFinderPreferences.LIMITACCEPTABLETEMPS);
		List<String> list_id = new ArrayList<String>();
		for(IdMusicScore mscore : list_music_score){
			list_id.add(mscore.getIdMusic());
		}
		System.out.println("taille list_id : "+range+" = "+list_id.size());
		return list_id;
	}

	@SuppressWarnings("unchecked")
	static List<String> getListIdStringByRangeInStats_Cache(String range){
		List<String> list_id = new ArrayList<String>();
		MongoCollection<Document> collection_stats_cache = ms.getCollection(range);
		GregorianCalendar gc = new GregorianCalendar();
		String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
		Document doc = new Document(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE,new Document("$eq",week));
		MongoCursor<Document> cursor = ms.findBy(collection_stats_cache, doc);
		if(cursor.hasNext()){
			Document doc_weeks = cursor.next();
			List<Document> list_age_range = (List<Document>) doc_weeks.get(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE);
			for(Document doc_range : list_age_range){
				if(doc_range.getString(MongoCollectionsAndKeys.AGE_STATS_CACHE).equals(range)){
					List<Document> doc_musics = (List<Document>) doc_range.get(MongoCollectionsAndKeys.MUSICS_STATS_CACHE);
					for(Document d : doc_musics){
						list_id.add(d.getString(MongoCollectionsAndKeys.MUSIC_ID_STATS_CACHE));
					}
				}
			}
		}
		return list_id;
	}

	static List<MFMusicDTO> getListMFMusicDTOMostPopular(String range){
		MongoCollection<Document> collection_musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		List<String> list_id = getListIdStringByRangeInStats_Cache(range);
		List<MFMusicDTO> list_music_dto = new ArrayList<MFMusicDTO>();

		for(String id : list_id){
			Document findQuery = new Document(MongoCollectionsAndKeys.IDMUSIC_MUSICS, new Document("$eq",id));
			MongoCursor<Document> cursor_music = ms.findBy(collection_musics, findQuery);	
			if(cursor_music.hasNext()){
				Document music_doc = cursor_music.next();
				System.out.println(music_doc);
				list_music_dto.add((MFMusicDTO) MongoUtils.transformDocumentIntoMFMusic(music_doc));
			}
		}
		return list_music_dto;
	}


	// Méthode retournant la tranche d'âge correspondante à l'âge de l'utilisateur dans la collection STATS 
	static String getRangeByAge(int age){
		if(age<18)
			return MongoCollectionsAndKeys.MINUSEIGHTEEN_STATS;
		if(age<25)
			return MongoCollectionsAndKeys.MINUSTWENTYFIVE_STATS;
		if(age<50)
			return MongoCollectionsAndKeys.MINUSFIFTY_STATS;
		else
			return MongoCollectionsAndKeys.PLUSFIFTY_STATS;
	}

	/* Méthode appelé par le deamon afin de remplir la collection STATS_CACHE toutes les heures*/
	public static void addListIdMusicMostPopularAllRange(){
		System.out.println("Début appel addList");
		addListIdMusicMostPopularByRange(MongoCollectionsAndKeys.MINUSEIGHTEEN_STATS);
		addListIdMusicMostPopularByRange(MongoCollectionsAndKeys.MINUSTWENTYFIVE_STATS);
		addListIdMusicMostPopularByRange(MongoCollectionsAndKeys.MINUSFIFTY_STATS);
		addListIdMusicMostPopularByRange(MongoCollectionsAndKeys.PLUSFIFTY_STATS);
		addListIdMusicMostPopularByRange(MongoCollectionsAndKeys.GENERAL_STATS_CACHE);
		System.out.println("Fin appel addList");
	}
	
	public List<MFMusicDTO> getListMFMusicDTOMostPopularAllRange(){
		MongoCollection<Document> collection_musics = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		List<String> list_id = getListIdStringByRangeInStats_Cache(MongoCollectionsAndKeys.GENERAL_STATS_CACHE);
		List<MFMusicDTO> list_music_dto = new ArrayList<MFMusicDTO>();

		for(String id : list_id){
			Document findQuery = new Document(MongoCollectionsAndKeys.IDMUSIC_MUSICS, new Document("$eq",id));
			MongoCursor<Document> cursor_music = ms.findBy(collection_musics, findQuery);	
			if(cursor_music.hasNext()){
				Document music_doc = cursor_music.next();
				System.out.println(music_doc);
				list_music_dto.add((MFMusicDTO) MongoUtils.transformDocumentIntoMFMusic(music_doc));
			}
		}
		return list_music_dto;
	}

	@SuppressWarnings("unchecked")
	static void addListIdMusicMostPopularByRange(String range){
		System.out.println("addList : "+range);
		List<String> list_id_music = new ArrayList<String>();
		if(range.equals(MongoCollectionsAndKeys.GENERAL_STATS_CACHE))
			list_id_music= getListIdMostPopularAllRangeInStats(range);
		else
			list_id_music = getListIdMostPopularByRangeInStats(range);

		if(list_id_music.isEmpty())return;
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.STATS_CACHE);
		GregorianCalendar gc = new GregorianCalendar();
		String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
		Document doc = new Document(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE,new Document("$eq",week));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		Document old = new Document();
		Document fin = new Document();
		if(cursor.hasNext()){
			fin.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE, week);
			old.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE, week);
			Document age_range_doc = cursor.next();
			List<Document> list_age_range =  (List<Document>) age_range_doc.get(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE);
			old.append(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE, list_age_range);
			for(Document doc_range : list_age_range){
				if(doc_range.getString(MongoCollectionsAndKeys.AGE_STATS_CACHE).equals(range)){
					Document age_range = new Document();
					age_range.put(MongoCollectionsAndKeys.AGE_STATS_CACHE,range);
					List<Document> list_id_music_doc = new ArrayList<Document>();
					for(String id : list_id_music){
						Document doc_id = new Document();
						doc_id.put(MongoCollectionsAndKeys.MUSIC_ID_STATS_CACHE, id);
						list_id_music_doc.add(doc_id);
					}
					age_range.put(MongoCollectionsAndKeys.MUSICS_STATS_CACHE,list_id_music_doc);
					List<Document> list_range = new ArrayList<Document>();
					list_range.add(age_range);
					for(Document doc_tmp : (ArrayList<Document>)old.get(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE))
						if(!(list_range.contains(doc_tmp)))
							list_range.add(doc_tmp);
					fin.append(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE,list_range);
					ms.replaceOne(collection, old, fin);
					return;
				}
			}

			Document age_range = new Document();
			age_range.put(MongoCollectionsAndKeys.AGE_STATS_CACHE,range);
			List<Document> list_id_music_doc = new ArrayList<Document>();
			for(String id : list_id_music){
				Document doc_id = new Document();
				doc_id.put(MongoCollectionsAndKeys.MUSIC_ID_STATS_CACHE, id);
				list_id_music_doc.add(doc_id);
			}
			age_range.put(MongoCollectionsAndKeys.MUSICS_STATS_CACHE,list_id_music_doc);
			List<Document> list_range = new ArrayList<Document>();
			list_range.add(age_range);
			for(Document doc_tmp : (ArrayList<Document>)old.get(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE))
				if(!(list_range.contains(doc_tmp)))
					list_range.add(doc_tmp);
			fin.append(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE,list_range);
			ms.replaceOne(collection, old, fin);
			return;
		}
		else{
			Document new_doc = new Document();
			new_doc.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE, week);
			Document age_range = new Document();
			age_range.put(MongoCollectionsAndKeys.AGE_STATS_CACHE,range);
			List<Document> list_id_music_doc = new ArrayList<Document>();
			for(String id : list_id_music){
				Document doc_id = new Document();
				doc_id.put(MongoCollectionsAndKeys.MUSIC_ID_STATS_CACHE, id);
				list_id_music_doc.add(doc_id);
			}
			age_range.put(MongoCollectionsAndKeys.MUSICS_STATS_CACHE,list_id_music_doc);
			List<Document> list_range = new ArrayList<Document>();
			list_range.add(age_range);
			new_doc.put(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE,list_range);
			ms.insertOne(collection, new_doc);
			return;
		}
	}


	// Méthode permettant de rajouter dans la collection Statistique le résultat de la recherche de l'utilisateur.
	@SuppressWarnings("unchecked")
	static void addNewSearch(String idMusic, Date userBirth){
		int age = MathUtils.calculAge(userBirth);
		String range= getRangeByAge(age);
		int test = 0;
		Document fin = new Document();
		Document old = new Document();
		GregorianCalendar gc = new GregorianCalendar();
		String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));

		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.STATS);

		Document doc = new Document(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS,new Document("$eq",week));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		if(cursor.hasNext()){
			fin.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS, week);
			old.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS, week);
			Document age_range_doc = cursor.next();
			List<Document> list_age_range =  (List<Document>) age_range_doc.get(MongoCollectionsAndKeys.AGERANGE_STATS);
			old.append(MongoCollectionsAndKeys.AGERANGE_STATS, list_age_range);	
			for(Document doc_range : list_age_range)
				if(doc_range.getString(MongoCollectionsAndKeys.AGE_STATS).equals(range)){
					List<Document> list_id_score = (List<Document>) doc_range.get(MongoCollectionsAndKeys.MUSICS_STATS);
					List<Document> list = new ArrayList<Document>();
					for(Document d : list_id_score){
						if(d.get(MongoCollectionsAndKeys.IDMUSIC_STATS).equals(idMusic)){
							int val = d.getInteger(MongoCollectionsAndKeys.SCOREMUSIC_STATS);
							val = val + 1;
							test = 1;
							Document doc1 = new Document();
							doc1.put(MongoCollectionsAndKeys.IDMUSIC_STATS, idMusic);
							doc1.append(MongoCollectionsAndKeys.SCOREMUSIC_STATS, val);
							list.add(doc1);
						}
						else
							list.add(d);
					}
					if(test == 1){		
						Document age_range = new Document();
						age_range.put(MongoCollectionsAndKeys.AGE_STATS,range);
						age_range.put(MongoCollectionsAndKeys.MUSICS_STATS,list);
						List<Document> list_range = new ArrayList<Document>();
						list_range.add(age_range);
						fin.append(MongoCollectionsAndKeys.AGERANGE_STATS,list_range);
						ms.replaceOne(collection, old, fin);
						return;
					}
					if(test == 0){
						Document tmp = new Document();
						tmp.put(MongoCollectionsAndKeys.IDMUSIC_STATS, idMusic);
						tmp.append(MongoCollectionsAndKeys.SCOREMUSIC_STATS, 1);
						list.add(tmp);
						Document age_range = new Document();
						age_range.put(MongoCollectionsAndKeys.AGE_STATS,range);
						age_range.put(MongoCollectionsAndKeys.MUSICS_STATS,list);
						List<Document> list_range = new ArrayList<Document>();
						list_range.add(age_range);
						fin.append(MongoCollectionsAndKeys.AGERANGE_STATS,list_range);
						ms.replaceOne(collection, old, fin);
						return;
					}
				}
			Document age_range = new Document();
			Document id_score = new Document();
			id_score.put(MongoCollectionsAndKeys.IDMUSIC_STATS,idMusic);
			id_score.put(MongoCollectionsAndKeys.SCOREMUSIC_STATS,1);
			List<Document> list_id_score = new ArrayList<Document>();
			list_id_score.add(id_score);
			age_range.put(MongoCollectionsAndKeys.AGE_STATS,range);
			age_range.put(MongoCollectionsAndKeys.MUSICS_STATS,list_id_score);
			List<Document> l_age_range = new ArrayList<Document>();
			l_age_range.add(age_range);
			l_age_range.addAll((ArrayList<Document>) old.get(MongoCollectionsAndKeys.AGERANGE_STATS));
			fin.append(MongoCollectionsAndKeys.AGERANGE_STATS,l_age_range);
			ms.replaceOne(collection,old,fin);
			return;

		}else{
			Document doc_stat = new Document();
			doc_stat.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS, week);
			Document id_score = new Document();
			id_score.put(MongoCollectionsAndKeys.IDMUSIC_STATS,idMusic);
			id_score.put(MongoCollectionsAndKeys.SCOREMUSIC_STATS,1);
			List<Document> list_id_score = new ArrayList<Document>();
			list_id_score.add(id_score);
			Document age_range = new Document();
			age_range.put(MongoCollectionsAndKeys.AGE_STATS,range);
			age_range.put(MongoCollectionsAndKeys.MUSICS_STATS,list_id_score);
			List<Document> list_range = new ArrayList<Document>();
			list_range.add(age_range);
			doc_stat.put(MongoCollectionsAndKeys.AGERANGE_STATS,list_range);
			ms.insertOne(collection, doc_stat);
			return;
		}

	}

	public static void deleteCacheUserExceedOneHour(){
		long timeNow = new Date().getTime() - TimeInMilliSeconds.HOUR.value;
		Document doc;
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.CACHE);
		doc = new Document("time", new Document("$lt",timeNow));
		ms.deleteMany(collection, doc);
	}


	public static void main(String[] args){
		//MySQLService mysql = MySQLService.getInstance();
		//User user = mysql.checkConnexion("llll", "llll");
		//User user1 = mysql.checkConnexion("aaaa", "aaaa");
		//addNewSearch("1138154490", user1.getDateBirth());
		//addNewSearch("100015321", user.getDateBirth());
		//addListIdMusicMostPopularByRange("-25");
		/*	List<MFMusicDTO> list = getIdMusicMostPopular(MongoCollectionsAndKeys.MINUSEIGHTEEN_STATS); 
		System.out.println(list);
		for(MFMusicDTO m : list)
			System.out.println(m);*/
		addListIdMusicMostPopularAllRange();
		//	addListIdMusicMostPopularAllRange();
	}
}