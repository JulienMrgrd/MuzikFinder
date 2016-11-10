package nosql;

import java.util.Date;
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
		mongo = MongoService.getInstance();
		// éventuellement cassandra = new CassandraService() ;
		// éventuellement dynamo = new DynamoDBService();
	}

	//////////////PARTIE INSERT///////////////
	public boolean insertMusicIfNotExists(String musicId, String lyrics, String artistId, String artistName,
			String albumId, String albumName, String nameMusic, String language, String spotifyId, 
			String soundCloudId, String genre){
		return mongo.insertMusicIfNotExists(musicId, lyrics, artistId, artistName, albumId, albumName, nameMusic, language, spotifyId, soundCloudId, genre);
	}

	public void insertTagIfNotExists(String tag, Integer nbOccur, String musicId){
		mongo.insertTagIfNotExists(tag, nbOccur, musicId);
	}

	public boolean insertIdAlbumIfNotExist(String idAlbum){
		return mongo.insertIdAlbumIfNotExist(idAlbum);
	}

	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum) {
		try {
			mongo.insertNewMusics(mapAlbumIdWithAlbum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertCacheSearchUser(List<String> tags, List<String> idMusics, String idRecherche){
		mongo.insertCacheSearchUser(tags, idMusics, idRecherche);
	}
	
	public void addListIdMusicMostPopularAllRange(){
		mongo.addListIdMusicMostPopularAllRange();
	}

	///////////////PARTIE CONTAINS//////////////////
	public boolean containsMusic(String musicId){
		return mongo.containsMusic(musicId);
	}
	
	public boolean containsTag(String tag) {
		return mongo.containsTag(tag);
	}

	public boolean containsIdMusicInTag(String tag, String idMusic){
		return mongo.containsIdMusicInTag(tag, idMusic);
	}
	
	public boolean containsIdAlbum(String idAlbum) {
		return mongo.containsIdAlbum(idAlbum);
	}

	public boolean containsIdRecherche(String idRecherche){
		return mongo.containsIdRecherche(idRecherche);
	}

	//////////////PARTIE GETTER///////////////////////
	public List<String> getIdMusicsByTag(String tag) {
		return mongo.getIdMusicsByTag(tag);
	}

	public List<String> getIdMusicsByIdArtist(String idArtist){
		return mongo.getIdMusicsByIdArtist(idArtist);
	}

	public List<String> getIdMusicsByChainWords(String chainWords){
		return mongo.getIdMusicsByChainWords(chainWords);
	}

	public List<String> getAllAlbumIds(){
		return mongo.getAllAlbumIds();
	}
	
	public MFMusic getMusicById(String idMusic) {
		return mongo.getMusicById(idMusic);
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
	
	public List<MFMusic> searchMusicsByTagsInTags(List<String> tags, String idRecherche){
		return mongo.searchMusicsByTagsInTags(tags, idRecherche);
	}

	/**
	 * Cette méthode permet de chercher les musics correspondantes au tags entrés par
	 * l'utilisateur en traitant les tags comme une phrase complète
	 * @param tags
	 * @return
	 */
	public List<String> matchMusicsWithTags(List<String> tags){
		return mongo.matchMusicsWithTags(tags);
	}

	public List<MFMusic> searchMusicsByTagsInLyrics(List<String> tags, String idRecherche){
		return mongo.searchMusicsByTagsInLyrics(tags, idRecherche);
	}

	///////////////PARTIE SEARCH USER/////////////////////////
	public void addNewSearch(String idMusic, Date userBirth){
		mongo.addNewSearch(idMusic, userBirth);
	}
	
	public List<MFMusic> getTopMusicSearchThisWeek(){
		return mongo.getTopMusicSearchThisWeek();
	}
	
	public List<MFMusic> getTopMusicSearchThisMonth(){
		return mongo.getTopMusicSearchThisMonth();
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
