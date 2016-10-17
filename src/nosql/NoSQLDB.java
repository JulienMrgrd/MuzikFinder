package nosql;


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
		mongo = new MongoService();
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
	
	
	public Set<String> getIdMusicsByIdArtist(String idArtist){
		return mongo.getIdMusicsByIdArtist(idArtist);
	}
	
	public Set<String> getIdMusicsByTag(String tag) {
		return mongo.getIdMusicsByTag(tag);
	}
	
	public String getIdArtist(String nameArtiste){
		return mongo.getIdArtist(nameArtiste);
	}
	
	public Set<String> getIdMusicsByChainWords(String chainWords){
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

}
