package api.musixMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import api.musixMatch.utils.MusixMatchAPIHelper;
import api.musixMatch.utils.MusixMatchConstants;
import api.musixMatch.utils.RequestHelper;
import interfaces.MFArtist;
import interfaces.MFMusic;

public class MusixMatchService {

	private MusixMatchService() {}
	
	/** Instance unique préinitialisée */
	private static MusixMatchService INSTANCE = new MusixMatchService();
 
	/** Technique du double-cheking */
	public static MusixMatchService getInstance(){	
		if (INSTANCE == null){ 	
			synchronized(MusixMatchService.class){
				if (INSTANCE == null){
					INSTANCE = new MusixMatchService();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Récupère la liste des musiques (avec paroles) d'un album
	 * API url example : http://api.musixmatch.com/ws/1.1/album.musics.get?apikey=f29172a320a83fa2eae8802fa44cbb01&album_id=16742456&page=1&page_size=100
	 * @param albumId
	 * @return
	 */
	public List<MFMusic> getMusicsInAlbum(String albumId) {
		Map<String, String> params = new HashMap<>();

		params.put(MusixMatchConstants.PAGE_SIZE, Integer.toString(MusixMatchConstants.MAX_PAGE) );
		params.put(MusixMatchConstants.ALBUM_ID, albumId);
		String request = RequestHelper.createRequest(MusixMatchConstants.ALBUM_TRACKS_GET, params);
		String response = RequestHelper.sendRequest(request);
		
		List<MFMusic> musics = MusixMatchAPIHelper.getMusicsList(response);
		if(musics==null) return null;
		musics.forEach(music -> addLyricsToMusic(music)); // add Lyrics from API
		
		return musics;
	}

	private void addLyricsToMusic(MFMusic music) {
		if( !music.getHasLyrics().equals(MusixMatchConstants.NO_LYRICS) ){
			Map<String, String> params = new HashMap<>();

			params.put(MusixMatchConstants.TRACK_ID, music.getTrackId());
			String request = RequestHelper.createRequest(MusixMatchConstants.TRACK_LYRICS_GET, params);
			String response = RequestHelper.sendRequest(request);
			
			music.setLyrics( MusixMatchAPIHelper.getLyrics(response) );
		}
	}

	public List<MFMusic> getTopMusics(int from, int to, String country) {
		if(from<=0) from = 1;
		Set<MFMusic> res = new HashSet<MFMusic>(to); // évite les doublons
		
		Map<String, String> params = new HashMap<>(3);
		params.put(MusixMatchConstants.COUNTRY, country);

		int quotient = to/100;
		int modulo = to%100;
		
		// Si to>100, on boucle autant de fois qu'il y a de paquet de 100 (contrainte de l'API), et on passe à la page suivante
		for(int i=0; i<quotient; i++){
			params.put(MusixMatchConstants.PAGE_SIZE, Integer.toString(100));
			params.put(MusixMatchConstants.PAGE, Integer.toString(from));
			String request = RequestHelper.createRequest(MusixMatchConstants.TRACK_CHART_GET, params);
			String response = RequestHelper.sendRequest(request);
			res.addAll(MusixMatchAPIHelper.getMusicsList(response));
			from++;
		}
		
		params.put(MusixMatchConstants.PAGE_SIZE, Integer.toString(modulo));
		params.put(MusixMatchConstants.PAGE, Integer.toString(from));
		String request = RequestHelper.createRequest(MusixMatchConstants.TRACK_CHART_GET, params);
		String response = RequestHelper.sendRequest(request);
		res.addAll(MusixMatchAPIHelper.getMusicsList(response));
		
		return new ArrayList<MFMusic>(res);
	}
	
	public List<MFArtist> getTopArtists(int pos, int nbArtistsToGet, String country) {
		Map<String, String> params = new HashMap<>();

		params.put(MusixMatchConstants.PAGE_SIZE, Integer.toString(nbArtistsToGet));
		params.put(MusixMatchConstants.PAGE, Integer.toString(pos));
		params.put(MusixMatchConstants.COUNTRY, country);
		String request = RequestHelper.createRequest(MusixMatchConstants.ARTIST_CHART_GET, params);
		String response = RequestHelper.sendRequest(request);
		
		return MusixMatchAPIHelper.getArtistsList(response);
	}

}
