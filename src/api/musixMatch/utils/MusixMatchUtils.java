package api.musixMatch.utils;

import java.util.List;

import api.musixMatch.metier.Album;
import utils.MuzikFinderConstants;

public final class MusixMatchUtils {

	private MusixMatchUtils(){ }
	
	/**
	 * Return true if one Album in the list as the same artist_name,album_name,release_date & album_track_count
	 * @param toCompare
	 * @param albums
	 * @return
	 */
	public static boolean containsSameAlbum(Album toCompare, List<Album> albums) {
		if(toCompare==null || albums==null || albums.isEmpty()) return false;
		for(Album alb : albums){
			if(alb.getArtistName().equalsIgnoreCase(toCompare.getArtistName()) &&
				alb.getAlbumName().equalsIgnoreCase(toCompare.getAlbumName()) &&
				alb.getAlbumReleaseDate().equalsIgnoreCase(toCompare.getAlbumReleaseDate()) &&
				alb.getAlbumTrackCount().equalsIgnoreCase(toCompare.getAlbumTrackCount()) ){
				return true;
			}
		}
		return false;
	}

	public static boolean isAnAlbum(Album alb) {
		return alb.getAlbumReleaseType().equalsIgnoreCase(MuzikFinderConstants.ALBUM);
	}

}
