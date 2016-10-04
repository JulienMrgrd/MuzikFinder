package server.services;

import java.util.List;

/**
 * Aucun appel direct aux BD ou Api ici !!
 * @author JulienM
 *
 */
public class MuzikFinderService {
	
	//private NoSQLDB nosql; singleton !
	//private SQLDB sql; singleton !
	//private APIService api; singleton !
	
	public MuzikFinderService(){
		/*
		 nosql = new NoSQLDB(); (va instancier ou récupérer le singleton du NoSQL, Mongo ou autre) 
		 sql = new SQLDB(); (va instancier ou récupérer le singleton du SQL, MySQL ou autre) 
		 api = new APIService(); (va instancier ou récupérer le singleton de l'API, MusixMatch ou autre) 
		 */
	}
	
	public void insertNewLyricsProcess(int nbLyrics){
		/**
		 * TODO: utiliser les fonctions private (en suivant le diag de séquence)
		 */
	}
	
	private List<String> getRandomArtistsFromAPI(){
		//TODO : api.getBLABBLA
		return null;
	}
	
	private boolean containsArtistsInNoSQL(String artist){
		//TODO : nosql.containsBLABBLA
		return true;
	}
	
	private List<String> getMusicsFromAPI(String artist /* artistID?? */){
		//TODO : api.getBLABBLA
		return null;
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
