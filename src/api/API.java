package api;

import java.util.List;

import api.musixMatch.MusixMatchService;
import interfaces.MFArtist;
import interfaces.MFMusic;

/**
 * Appel les fonctions de l'API choisies (MusixMatch, SoundcloudAPI, etc ...)
 * @author JulienM
 */
public class API {
	
	MusixMatchService musixMatch;

	public API() {
		musixMatch = new MusixMatchService();
	}
	
	public List<String> getAllAlbumIds(String artistId) {
		return musixMatch.getAllAlbumIds(artistId);
	}

	public List<MFMusic> getMusicsInAlbum(String albumId) {
		return musixMatch.getMusicsInAlbum(albumId);
	}
	
	public List<MFArtist> getTopArtists(int pos, int nbArtistsToGet, String country) {
		return musixMatch.getTopArtists(pos, nbArtistsToGet, country);
	}

	public List<MFMusic> getTopMusics(int from, int to, String country) {
		return musixMatch.getTopMusics(from, to, country);
	}

}
