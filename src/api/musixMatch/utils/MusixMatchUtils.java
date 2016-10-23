package api.musixMatch.utils;

import java.util.List;

import api.musixMatch.metier.Album;
import utils.MuzikFinderConstants;

public final class MusixMatchUtils {

	/** Examples of the end tag display by MusixMatch, with a size of 75 characters <br>
	 * 	\n...\n\n******* This Lyrics is NOT for Commercial use *******\n(1409613097569)
	 *  <br>
	 *  \n...\n\n******* This Lyrics is NOT for Commercial use *******\n(1409613134386)
	 */
	public static final int SIZE_OF_LYRICS_END = 75;

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

	/**
	 * Check if it's an Album or not (can be a Single, for example)
	 * @param alb
	 * @return
	 */
	public static boolean isAnAlbum(Album alb) {
		return alb.getAlbumReleaseType().equalsIgnoreCase(MuzikFinderConstants.ALBUM);
	}

}
