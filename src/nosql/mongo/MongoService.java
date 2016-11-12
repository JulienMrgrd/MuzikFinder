package nosql.mongo;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import interfaces.MFMusic;
import utils.MuzikFinderPreferences;
import utils.TimeInMilliSeconds;

public class MongoService {

	private ServerAddress serverAddress;
	private MongoCredential mongoCredential;
	private static MongoClient mongoClient;
	private MongoDatabase db;

	/** Constructeur privé */
	private MongoService(){
		if(MuzikFinderPreferences.LOGS){
			Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING); // e.g. or Log.WARNING, etc.
		}
		// Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
		serverAddress = new ServerAddress("ds049456.mlab.com", 49456);
		mongoCredential = MongoCredential.createCredential("heroku_1dpqh3kq", "heroku_1dpqh3kq",
				"gv7mru79jbtgn52lrl5mg301qh".toCharArray());
		mongoClient = new MongoClient(serverAddress, Arrays.asList(mongoCredential));
		db = mongoClient.getDatabase("heroku_1dpqh3kq");
	}
 
	/** Instance unique préinitialisée */
	private static MongoService INSTANCE = getInstance();
 
	/** Technique du double-cheking */
	public static MongoService getInstance(){	
		if (INSTANCE == null){ 	
			synchronized(MongoService.class){
				if (INSTANCE == null){
					INSTANCE = new MongoService();
				}
			}
		}
		return INSTANCE;
	}


	// this creates collection if not exists
	protected void insertOne(MongoCollection<Document> collection, Document doc){
		collection.insertOne(doc);
	}

	// this creates collection if not exists
	void updateOne(MongoCollection<Document> collection, Document before, Document after){
		collection.updateOne(before, after);
	}

	MongoCursor<Document> findAll(MongoCollection<Document> collection){
		return collection.find().iterator();
	}
	
	Document findFirst(MongoCollection<Document> collection){
		try {
			return collection.find().limit(1).first();
		} catch (Exception e){
			return null;
		}
	}
	
	MongoCursor<Document> findBy(MongoCollection<Document> collection, Document findQuery){
		return collection.find(findQuery).iterator();
	}

	void replaceOne(MongoCollection<Document> collection, Document before, Document after){
		collection.replaceOne(before, after);
	}
	
	public void addListIdMusicMostPopularAllRanges(){
		MongoServiceSearchUser.addListIdMusicMostPopularAllRanges();
	}
	
	MongoCursor<Document> findBy(MongoCollection<Document> collection, 
			Document findQuery, Document orderBy){
		return collection.find(findQuery).sort(orderBy).iterator();
	}

	MongoCollection<Document> getCollection(String collectionName){
		return db.getCollection(collectionName);
	}

	void deleteMany(MongoCollection<Document> collection, 
			Document findQuery){
		collection.deleteMany(findQuery);
	}

	//////////////PARTIE INSERT///////////////
	boolean insertMusicIfNotExists(String musicId, String lyrics, String artistId, String artistName,
			String albumId, String albumName, String nameMusic, String language, String spotifyId, 
			String soundCloudId, String genre){
		
		return MongoServiceInsert.insertMusicIfNotExists(musicId, lyrics, artistId, artistName, albumId, albumName, nameMusic, language, spotifyId, soundCloudId, genre);
	}

	void insertTagIfNotExists(String tag, Integer nbOccur, String musicId){
		MongoServiceInsert.insertTagIfNotExists(tag, nbOccur, musicId);
	}

	boolean insertIdAlbumIfNotExist(String idAlbum){
		return MongoServiceInsert.insertIdAlbumIfNotExist(idAlbum);
	}
	
	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum){
		MongoServiceInsert.insertNewMusics(mapAlbumIdWithAlbum);
	}
	
	void insertCacheSearchUser(List<String> tags, List<String> idMusics, String idRecherche){
		MongoServiceInsert.insertCacheSearchUser(tags, idMusics, idRecherche);
	}

	///////////////PARTIE CONTAINS//////////////////
	boolean containsMusic(String musicId){
		return MongoServiceContains.containsMusic(musicId);
	}

	boolean containsTag(String tag){
		return MongoServiceContains.containsTag(tag);
	}

	boolean containsIdMusicInTag(String tag, String idMusic){
		return MongoServiceContains.containsIdMusicInTag(tag, idMusic);
	}

	boolean containsIdAlbum(String idAlbum){
		return MongoServiceContains.containsIdAlbum(idAlbum);
	}
	
	boolean containsIdRecherche(String idRecherche){
		return MongoServiceContains.containsIdRecherche(idRecherche);
	}

	//////////////PARTIE GETTER///////////////////////
	public List<String> getAllAlbumIds(){
		return MongoServiceGetId.getAllAlbumIds();
	}
	
	public List<String> getListNameArtistBeginWith(String nameArtist){
		return MongoServiceGetId.getListNameArtistBeginWith(nameArtist);
	}
	
	public List<String> getListTrackNameBeginWith(String trackName){
		return MongoServiceGetId.getListTrackNameBeginWith(trackName);
	}

	//////////////PARTIE SEARCH////////////////////////
	/**
	 * Reduced the given list in parameter if one of these musics exists in Mongo 
	 * @param musics
	 * @return the reduced list
	 */
	public List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
		return MongoServiceSearchMusic.filterByExistingMusics(musics);
	}

	public List<MFMusic> searchMusics(List<String> tags, String idRecherche){
		return MongoServiceSearchMusic.searchMusics(tags, idRecherche);
	}
	
	public List<MFMusic> searchMusicsByTagsInTags(List<String> tags, String idRecherche){
		return MongoServiceSearchMusic.searchMusicsByTagsInTags(tags, idRecherche);
	}
	

	/**
	 * Cette méthode permet de chercher les musics correspondantes au tags entrés par
	 * l'utilisateur en traitant les tags comme une phrase complète
	 * @param tags
	 * @return
	 */
	public List<String> matchMusicsWithTags(List<String> tags){
		return MongoServiceSearchMusic.matchMusicsWithTags(tags);
	}

	public List<MFMusic> searchMusicsByTagsInLyrics(List<String> tags, String idRecherche){
		return MongoServiceSearchMusic.searchMusicsByTagsInLyrics(tags, idRecherche);
	}
	
	public List<MFMusic> getMoreResults(String idRecherche){
		return MongoServiceSearchMusic.getMoreResults(idRecherche);
	}

	public List<MFMusic> getMusicsByArtist(String artistName){
		return MongoServiceSearchMusic.getMusicsByArtist(artistName);
	}
	
	public List<MFMusic> getMusicsByTrackName(String trackName){
		return MongoServiceSearchMusic.getMusicsByTrackName(trackName);
	}
	
	///////////////PARTIE SEARCH USER/////////////////////////
	public void addNewSearch(String idMusic, LocalDate userBirth){
		MongoServiceSearchUser.addNewSearch(idMusic, userBirth);
	}
	
	public List<MFMusic> getListMFMusicMostPopularByRange(String range) {
		return MongoServiceSearchUser.getListMFMusicMostPopularByRange(range);
	}
	
	public List<MFMusic> getTopMusicSearchThisWeek(){
		return MongoServiceSearchUser.getTopMusicSearchByPeriod(TimeInMilliSeconds.WEEK);
	}
	
	public List<MFMusic> getTopMusicSearchThisMonth(){
		return MongoServiceSearchUser.getTopMusicSearchByPeriod(TimeInMilliSeconds.MONTH);
	}
	
	public void deleteCacheUserExceed(long time){
		MongoServiceSearchUser.deleteCacheUserExceed(time);
	}

	//////////////PARTIE PREFERENCE//////////////////////////
	public void setPref(String prefName, String param) {
		MongoServicePreference.setPref(prefName, param);
	}

	public String getPref(String prefName) {
		return MongoServicePreference.getPref(prefName);
	}
	
	public void close(){
		mongoClient.close();
	}

}