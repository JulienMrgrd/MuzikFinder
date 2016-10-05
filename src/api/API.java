package api;

import java.util.List;

import org.jmusixmatch.entity.track.Track;

import api.musixMatch.MusixMatchService;
import nosql.mongo.MongoService;

/**
 * Appel les fonctions de l'API choisies (MusixMatch, SoundcloudAPI, etc ...)
 * @author JulienM
 */
public class API {
	
	MusixMatchService musixMatch;

	public API() {
		musixMatch = new MusixMatchService();
	}
	
	public List<Track> getTracks(int nbToGet){
		return musixMatch.getTracks(nbToGet);
	}

}
