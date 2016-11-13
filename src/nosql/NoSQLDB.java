package nosql;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import interfaces.MFMusic;
import nosql.mongo.MongoService;

/**
 * Appel les fonctions de la BD choisies (MongoDB, DynamoDB, etc ...)
 * @author JulienM
 */
public class NoSQLDB {

	private MongoService mongo;
	// éventuellement private CassandraService cassandra;
	// éventuellement private DynamoDBService dynamo;

	public NoSQLDB() {
		mongo = MongoService.getInstance();
		// éventuellement cassandra = new CassandraService() ;
		// éventuellement dynamo = new DynamoDBService();
	}

	//////////////PARTIE INSERT///////////////
	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum) {
		try {
			mongo.insertNewMusics(mapAlbumIdWithAlbum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addListIdMusicMostPopularAllRanges(){
		mongo.addListIdMusicMostPopularAllRanges();
	}

	//////////////PARTIE GETTER///////////////////////
	public Set<String> getArtistNamesBeginWith(String nameArtist){
		return mongo.getArtistNamesBeginWith(nameArtist);
	}

	public Set<String> getTrackNamesBeginWith(String trackName){
		return mongo.getTrackNamesBeginWith(trackName);
	}
	
	//////////////PARTIE SEARCH///////////////////////
	/**
	 * Reduced the given list in parameter if one of these musics exists in Mongo 
	 * @param musics
	 * @return the reduced list
	 */
	public List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
		return mongo.filterByExistingMusics(musics);
	}

	public List<MFMusic> searchMusics(List<String> tags, String idRecherche){
		return mongo.searchMusics(tags, idRecherche);
	}

	public List<MFMusic> getMoreResults(String idRecherche){
		return mongo.getMoreResults(idRecherche);
	}

	public List<MFMusic> getMusicsByArtist(String artistName){
		return mongo.getMusicsByArtist(artistName);
	}
	
	public List<MFMusic> getMusicByTrackName(String trackName){
		return mongo.getMusicsByTrackName(trackName);
	}
	
	///////////////PARTIE SEARCH USER/////////////////////////
	public void addNewSearch(String idMusic, LocalDate userBirth){
		mongo.addNewSearch(idMusic, userBirth);
	}
	
	public List<MFMusic> getListMFMusicMostPopularByRange(String range) {
		return mongo.getListMFMusicMostPopularByRange(range);
	}
	
	public List<MFMusic> getTopMusicSearchThisWeek(){
		return mongo.getTopMusicSearchThisWeek();
	}
	
	public List<MFMusic> getTopMusicSearchThisMonth(){
		return mongo.getTopMusicSearchThisMonth();
	}

	public void deleteCacheUserExceed(long time){
		mongo.deleteCacheUserExceed(time);
	}
	
	//////////////PARTIE PREFERENCE//////////////////////////
	/** Get a pref in nosql database by name
	 * @param param
	 * @return
	 */
	public String getPref(String prefName) {
		return mongo.getPref(prefName);
	}

	/**
	 * Set a pref in nosql database by name
	 * @param prefName
	 * @param param
	 */
	public void setPref(String prefName, String param) {
		mongo.setPref(prefName, param);
	}
}
