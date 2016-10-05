package server.services;

import java.util.List;

import org.jmusixmatch.entity.track.Track;

import api.API;
import nosql.NoSQLDB;
import sql.SQLDB;

/**
 * Aucun appel direct aux BD ou Api ici !!
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
	
	private List<String> getRandomArtistsFromAPI(){
		//TODO : api.getBLABBLA
		return null;
	}
	
	private boolean containsArtistsInNoSQL(String artist){
		//TODO : nosql.containsBLABBLA
		return true;
	}
	
	public List<Track> getTracksFromAPI(int nbTracksToGet){
		return api.getTracks(nbTracksToGet);
	}
	
	private List<String> getTopMusicsFromAPI(int from, int to){
		//TODO : api.getBLABBLA
		return null;
	}
	
	private List<String> extractImportantWords(String lyrics){
		//TODO: utiliser TextParser du package utils.textMining;
		return null;
	}

}
