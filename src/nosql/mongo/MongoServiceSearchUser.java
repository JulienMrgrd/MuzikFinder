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
import sql.metier.User;
import sql.mysql.MySQLService;
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
	static List<String> getListIdMostPopularByRangeInStats(String range){
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
		if(list_music_score.size()> MuzikFinderPreferences.LIMITACCEPTABLETEMPS)
			list_music_score.subList(0, MuzikFinderPreferences.LIMITACCEPTABLETEMPS);
		List<String> list_id = new ArrayList<String>();
		for(IdMusicScore mscore : list_music_score){
			list_id.add(mscore.getIdMusic());
		}
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


	@SuppressWarnings("unchecked")
	static void addListIdMusicMostPopularByRange(String range){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.STATS_CACHE);
		GregorianCalendar gc = new GregorianCalendar();
		String week=(gc.get(Calendar.WEEK_OF_YEAR)+"-"+gc.get(Calendar.YEAR));
		Document doc = new Document(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS,new Document("$eq",week));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		Document old = new Document();
		Document fin = new Document();
		if(cursor.hasNext()){
			fin.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE, week);
			old.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE, week);
			Document age_range_doc = cursor.next();
			List<Document> list_age_range =  (List<Document>) age_range_doc.get(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE);
			old.append(MongoCollectionsAndKeys.AGERANGE_STATS, list_age_range);
			for(Document doc_range : list_age_range){
				if(doc_range.getString(MongoCollectionsAndKeys.AGE_STATS_CACHE).equals(range)){
					List<String> list_id_music = getListIdMostPopularByRangeInStats(range);
					Document age_range = new Document();
					age_range.put(MongoCollectionsAndKeys.AGE_STATS_CACHE,range);List<Document> list_id_music_doc = new ArrayList<Document>();
					for(String id : list_id_music){
						Document doc_id = new Document();
						doc_id.put(MongoCollectionsAndKeys.MUSIC_ID_STATS_CACHE, id);
						list_id_music_doc.add(doc_id);
					}
					age_range.put(MongoCollectionsAndKeys.MUSICS_STATS_CACHE,list_id_music_doc);
					List<Document> list_range = new ArrayList<Document>();
					list_range.add(age_range);
					fin.append(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE,list_range);
					ms.replaceOne(collection, old, fin);
					return;
				}
			}
			List<String> list_id_music = getListIdMostPopularByRangeInStats(range);
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
			fin.append(MongoCollectionsAndKeys.AGERANGE_STATS_CACHE,list_range);
			ms.replaceOne(collection, old, fin);
			return;
		}
		else{
			Document new_doc = new Document();
			new_doc.put(MongoCollectionsAndKeys.DATEWEEKSYEARS_STATS_CACHE, week);
			List<String> list_id_music = getListIdMostPopularByRangeInStats(range);
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
	//	MySQLService mysql = MySQLService.getInstance();
//		User user = mysql.checkConnexion("llll", "llll");
	//	User user1 = mysql.createNewUser("aaaa", "aaaa", "dada@dada", 2000, 12, 5);
		//addNewSearch("113810490", user1.getDateBirth());
//		addNewSearch("10000010", user.getDateBirth());
		/*	List<MFMusicDTO> list = getIdMusicMostPopular(MongoCollectionsAndKeys.MINUSEIGHTEEN_STATS); 
		System.out.println(list);
		for(MFMusicDTO m : list)
			System.out.println(m);*/
	}
}