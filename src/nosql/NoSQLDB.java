package nosql;

import java.util.List;

/**
 * Appel les fonctions de la BD choisies (MongoDB, DynamoDB, etc ...)
 * @author JulienM
 */
public class NoSQLDB {
	
	//private MongoService mongo;

	public NoSQLDB() {
		// TODO singleton
	}
	
	public void insertLyricsIfNotExists( List<String> words, String musicId ){
		// mongo....
	}
	
	public boolean insertArtistIfNotExist( String artistName, String artistId ){
		// mongo....
		return true;
	}

}
