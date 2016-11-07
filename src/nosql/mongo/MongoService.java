package nosql.mongo;

import java.util.Arrays;
import java.util.Date;
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
import server.dto.MusicDTO;
import utils.MuzikFinderPreferences;
import utils.TimeInMilliSeconds;

public class MongoService {

	ServerAddress serverAddress;
	MongoCredential mongoCredential;
	MongoClient mongoClient;
	MongoDatabase db;

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
	private static MongoService INSTANCE = new MongoService();
 
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
	protected void updateOne(MongoCollection<Document> collection, Document before, Document after){
		collection.updateOne(before, after);
	}

	protected MongoCursor<Document> findAll(MongoCollection<Document> collection){
		return collection.find().iterator();
	}
	
	protected Document findFirst(MongoCollection<Document> collection){
		try {
			return collection.find().limit(1).first();
		} catch (Exception e){
			return null;
		}
	}
	
	protected MongoCursor<Document> findBy(MongoCollection<Document> collection, Document findQuery){
		return collection.find(findQuery).iterator();
	}

	protected MongoCursor<Document> findBy(MongoCollection<Document> collection, 
			Document findQuery, Document orderBy){
		return collection.find(findQuery).sort(orderBy).iterator();
	}

	public MongoCollection<Document> getCollection(String collectionName){
		return db.getCollection(collectionName);
	}

	@SuppressWarnings("unused")
	private boolean dropCollection(String collectionName){
		try{
			db.getCollection(collectionName).drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean dropCollection(MongoCollection<Document> collection){
		try{
			collection.drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	//////////////PARTIE INSERT///////////////
	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId, String artistName,
			String nameMusic, String langue, String spotifyId, String soundCloudId){
		return MongoServiceInsert.insertLyricsIfNotExists(words, musicId, artistId, artistName, 
				nameMusic, langue, spotifyId, soundCloudId, getInstance());
	}

	public boolean insertTagIfNotExists(String tag, String musicId){
		return MongoServiceInsert.insertTagIfNotExists(tag, musicId, getInstance());
	}

	public boolean insertIdAlbumIfNotExist(String idAlbum){
		return MongoServiceInsert.insertIdAlbumIfNotExist(idAlbum,getInstance());
	}
	
	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum){
		MongoServiceInsert.insertNewMusics(mapAlbumIdWithAlbum, getInstance());
	}
	
	public void insertCacheSearchUser(List<String> tags, List<String> idMusics, String idRecherche){
		MongoServiceInsert.insertCacheSearchUser(tags, idMusics, getInstance(), idRecherche);
	}

	///////////////PARTIE CONTAINS//////////////////
	public boolean containsLyrics(String musicId){
		return MongoServiceContains.containsLyrics(musicId, getInstance());
	}

	public boolean containsTag(String tag){
		return MongoServiceContains.containsTag(tag, getInstance());
	}

	public boolean containsIdMusicInTag(String tag, String idMusic){
		return MongoServiceContains.containsIdMusicInTag(tag, idMusic, getInstance());
	}

	public boolean containsIdAlbum(String idAlbum){
		return MongoServiceContains.containsIdAlbum(idAlbum, getInstance());
	}
	
	public boolean containsIdRecherche(String idRecherche){
		return MongoServiceContains.containsIdRecherche(idRecherche, getInstance());
	}

	//////////////PARTIE GETTER///////////////////////
	public List<String> getIdMusicsByTag(String tag){
		return MongoServiceGetId.getIdMusicsByTag(tag, getInstance());
	}

	public List<String> getIdMusicsByIdArtist(String idArtist){
		return MongoServiceGetId.getIdMusicsByIdArtist(idArtist, getInstance());
	}

	public List<String> getIdMusicsByChainWords(String chainWords){
		return MongoServiceGetId.getIdMusicsByChainWords(chainWords, getInstance());
	}

	public List<String> getAllAlbumIds(){
		return MongoServiceGetId.getAllAlbumIds(getInstance());
	}


	//////////////PARTIE SEARCH////////////////////////
	public List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
		return MongoServiceSearchMusic.filterByExistingMusics(musics, getInstance());
	}

	public List<MusicDTO> searchMusics(List<String> tags, String idRecherche){
		return MongoServiceSearchMusic.searchMusics(tags, getInstance(), idRecherche);
	}
	
	public List<MusicDTO> searchMusicsByTagsInTags(List<String> tags, String idRecherche){
		return MongoServiceSearchMusic.searchMusicsByTagsInTags(tags, getInstance(), idRecherche);
	}

	/**
	 * Cette méthode permet de chercher les musics correspondantes au tags entrés par
	 * l'utilisateur en traitant les tags comme une phrase complète
	 * @param tags
	 * @return
	 */
	public List<String> matchMusicsWithTags(List<String> tags){
		return MongoServiceSearchMusic.matchMusicsWithTags(tags, getInstance());
	}

	public List<MusicDTO> searchMusicsByTagsInLyrics(List<String> tags, String idRecherche){
		return MongoServiceSearchMusic.searchMusicsByTagsInLyrics(tags, getInstance(), idRecherche);
	}
	
	public List<MusicDTO> getMoreResults(String idRecherche){
		return MongoServiceSearchMusic.getMoreResults(idRecherche, getInstance());
	}


	///////////////PARTIE SEARCH USER/////////////////////////
	public void addNewSearch(String idMusic, Date userBirth){
		MongoServiceSearchUser.addNewSearch(idMusic, userBirth, getInstance());
	}
	
	public List<MusicDTO> getTopMusicSearchThisWeek(){
		return MongoServiceSearchUser.getTopMusicSearchByPeriod(getInstance(), TimeInMilliSeconds.WEEK);
	}
	
	public List<MusicDTO> getTopMusicSearchThisMonth(){
		return MongoServiceSearchUser.getTopMusicSearchByPeriod(getInstance(), TimeInMilliSeconds.MONTH);
	}

	//////////////PARTIE PREFERENCE//////////////////////////
	public void setPref(String prefName, String param) {
		MongoServicePreference.setPref(prefName, param, getInstance());
	}

	public String getPref(String prefName) {
		return MongoServicePreference.getPref(prefName, getInstance());
	}
	
	public void close(){
		mongoClient.close();
	}

}