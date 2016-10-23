package nosql;

import java.util.List;
import java.util.Map;

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
		mongo = new MongoService(false);
		// éventuellement cassandra = new CassandraService() ;
		// éventuellement dynamo = new DynamoDBService();
	}
	

	public boolean insertTagIfNotExists(String tag, String musicId){
		return mongo.insertTagIfNotExists(tag, musicId);
	}
	
	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId, 
										String nameMusic, String langue, String spotifyId, String soundCloudId){
		return mongo.insertLyricsIfNotExists(words, musicId, artistId, nameMusic, langue, 
											spotifyId, soundCloudId);
	}

	public boolean insertArtistIfNotExist(String artistName, String artistId){
		return mongo.insertArtistIfNotExist(artistName, artistId);
	}

	public boolean insertIdAlbumIfNotExist(String idAlbum){
		return mongo.insertIdAlbumIfNotExist(idAlbum);
	}
	
	
	public boolean containsArtist(String artistId) {
		return mongo.containsArtist(artistId);
	}
	
	
	public List<String> getIdMusicsByIdArtist(String idArtist){
		return mongo.getIdMusicsByIdArtist(idArtist);
	}
	
	public List<String> getIdMusicsByTag(String tag) {
		return mongo.getIdMusicsByTag(tag);
	}
	
	public String getIdArtist(String nameArtiste){
		return mongo.getIdArtist(nameArtiste);
	}
	
	public List<String> getIdMusicsByChainWords(String chainWords){
		return mongo.getIdMusicsByChainWords(chainWords);
	}

	public List<String> getAllAlbumIds(){
		return mongo.getAllAlbumIds();
	}
	
	/**
	 * Reduced the given list in parameter if one of these musics exists in Mongo 
	 * @param musics
	 * @return the reduced list
	 */
	public List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
		return mongo.filterByExistingMusics(musics);
	}

	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum) {
		try {
			mongo.insertNewMusics(mapAlbumIdWithAlbum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Refer to MuzikFinderPreferences COUNTRY_ORDER array.
	 * @param pos
	 * @return
	 */
	public int getLastCountryPref() {
		return mongo.getLastCountryPref();
	}
	
	/**
	 * Refer to MuzikFinderPreferences COUNTRY_ORDER array.
	 * @param pos
	 * @return
	 */
	public void setLastCountryPref(int pos) {
		mongo.setLastCountryPref(pos);
	}

}
