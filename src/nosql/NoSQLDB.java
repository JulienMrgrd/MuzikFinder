package nosql;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

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
	
	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId, String nameMusic, String langue, String spotifyId, String soundCloudId){
		return mongo.insertLyricsIfNotExists(words, musicId, artistId, nameMusic, langue, spotifyId, soundCloudId);
	}

	public boolean insertArtistIfNotExist(String artistName, String artistId ){
		return mongo.insertArtistIfNotExist(artistName, artistId);
	}

	public boolean insertIdAlbumIfNotExist(String idAlbum){
		return mongo.insertIdAlbumIfNotExist(idAlbum);
	}
	
	public boolean presentArtist(String artistId){
		return mongo.presentArtist(artistId);
	}
	
	public boolean presentLyrics(String musicId){
		return mongo.presentLyrics(musicId);
	}
	
	public boolean presentTag(String tag){
		return mongo.presentTag(tag);
	}
	
	public boolean presentIdMusicOnTag(String tag, String idMusic){
		return mongo.presentIdMusicOnTag(tag, idMusic);
	}
	
	public boolean presentIdAlbum(String idAlbum){
		return mongo.presentIdAlbum(idAlbum);
	}

	public String getMusicsByTag(String tag){
		return mongo.getMusicsByTag(tag);
	}
	
	public Set<String> getMusicsByIdArtist(String idArtiste){
		return mongo.getMusicsByIdArtist(idArtiste);
	}
	
	public String getIdArtist(String nameArtiste){
		return mongo.getIdArtist(nameArtiste);
	}
	
	public Set<String> getMusicsByLyrics(String lyrics){
		return mongo.getMusicsByLyrics(lyrics);
	}

	public List<String> getAllIdAlbum(){
		return mongo.getAllIdAlbum();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
