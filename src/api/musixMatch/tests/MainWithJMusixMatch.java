package api.musixMatch.tests;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

public class MainWithJMusixMatch {

	public static void main(String[] args) throws MusixMatchException {
		String apiKey = "f29172a320a83fa2eae8802fa44cbb01";
		MusixMatch match = new MusixMatch(apiKey);
		String trackName = "Don't stop the Party";
		String artistName = "The Black Eyed Peas";

		// Track Search [ Fuzzy ]
		Track track = match.getMatchingTrack(trackName, artistName);
		TrackData data = track.getTrack();

		System.out.println("AlbumID : "    + data.getAlbumId());
		System.out.println("Album Name : " + data.getAlbumName());
		System.out.println("Artist ID : "  + data.getArtistId());
		System.out.println("Album Name : " + data.getArtistName());
		System.out.println("Track ID : "   + data.getTrackId());
	}
	
	public MainWithJMusixMatch() {
		// TODO Auto-generated constructor stub
	}
}
