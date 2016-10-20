package server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.API;
import interfaces.MFArtist;
import interfaces.MFMusic;
import nosql.NoSQLDB;
import sql.SQLDB;
import utils.MuzikFinderPreferences;

/**
 * Aucun appel direct aux BD ou API ici !!
 * @author JulienM
 *
 */
public class MuzikFinderService {
	
	private NoSQLDB nosql;
	private SQLDB sql;
	private API api;
	
	public MuzikFinderService(){
		 nosql = new NoSQLDB(); // (va instancier ou récupérer le singleton du NoSQL, Mongo ou autre) 
		 sql = new SQLDB(); // (va instancier ou récupérer le singleton du SQL, MySQL ou autre) 
		 api = new API(); // (va instancier ou récupérer le singleton de l'API, MusixMatch ou autre) 
	}
	
	
	////====== API PART ====== ////
	
	public List<MFArtist> getTopArtistsFromAPI(int pos, int nbArtistsToGet, String country) {
		return api.getTopArtists(pos, nbArtistsToGet, country);
	}
	
	public List<String> getAllAlbumIdsFromAPI(String artistId) {
		return api.getAllAlbumIds(artistId);
	}

	public List<MFMusic> getAllMusicsFromAPI(String albumId) {
		return api.getMusicsInAlbum(albumId);
	}
	
	private List<MFMusic> getTopMusicsFromAPI(int from, int to, String country){
		return api.getTopMusics(from, to, country);
	}
	
	
	//// ====== NOSQL PART ====== ////
	
	public boolean containsArtistInNoSQL(String artist){
		return nosql.containsArtist(artist);
	}
	
	public ArrayList<String> getIdMusicsByTagInNoSQL(String tag){
		return nosql.getIdMusicsByTag(tag);
	}
	
	public ArrayList<String> getIdMusicsByIdArtistInNoSQL(String artist){
		return nosql.getIdMusicsByIdArtist(artist);
	}
	
	public ArrayList<String> getIdMusicsByChainWordsInNoSQL(String lyrics){
		return nosql.getIdMusicsByChainWords(lyrics);
	}
	
	
	////====== SQL PART ====== ////
	
	
	////====== DAEMON PART ====== ////
	
	public void startFilingDatabaseProcess() {
		int lastCountry = nosql.getLastCountryPref();
		System.out.println("Pays visé : "+MuzikFinderPreferences.getCountry(lastCountry));
		List<MFMusic> musics = getTopMusicsFromAPI(0, MuzikFinderPreferences.MAX_TOP_TRACKS, MuzikFinderPreferences.getCountry(lastCountry));
		System.out.println(musics.size()+" musiques");
		List<MFMusic> musicsNotInNoSQL = nosql.filterByExistingMusics(musics);
		
		Map<String, List<MFMusic>> mapAlbumIdWithAlbum = new HashMap<>();
		int cpt = 0;
		for(MFMusic music : musicsNotInNoSQL){
			if(music!=null && music.getAlbumId()!=null){
				mapAlbumIdWithAlbum.put( music.getAlbumId(), getAllMusicsFromAPI(music.getAlbumId()) );
			}
			
			System.out.println("Album "+music.getAlbumId()+" = "+mapAlbumIdWithAlbum.get(music.getAlbumId()).size()+" musics...");
			cpt+=mapAlbumIdWithAlbum.get(music.getAlbumId()).size();
		}
		System.out.println("\nNombre de musiques récupérées au final : "+cpt);
		nosql.insertNewMusics(mapAlbumIdWithAlbum);
		nosql.setLastCountryPref( MuzikFinderPreferences.getNextPosition(lastCountry) );
	}

}
