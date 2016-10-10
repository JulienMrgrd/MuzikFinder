package api.musixMatch.metier;

import com.google.gson.annotations.SerializedName;

public class Music {

	@SerializedName("track_id")
	private String trackId;
	
	@SerializedName("track_name")
	private String trackName;
	
	@SerializedName("artist_id")
	private String artistId;
	
	@SerializedName("album_id")
	private String albumId;
	
	@SerializedName("track_spotify_id")
	private String trackSpotifyId;
	
	@SerializedName("track_soundcloud_id")
	private String trackSoundcloudId;
	
	@SerializedName("has_lyrics")
	private String hasLyrics;
	
	// Lyrics info
	private Lyrics lyrics;
	
	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getArtistId() {
		return artistId;
	}

	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}

	public String getTrackSpotifyId() {
		return trackSpotifyId;
	}

	public void setTrackSpotifyId(String trackSpotifyId) {
		this.trackSpotifyId = trackSpotifyId;
	}

	public String getTrackSoundcloudId() {
		return trackSoundcloudId;
	}

	public void setTrackSoundcloudId(String trackSoundcloudId) {
		this.trackSoundcloudId = trackSoundcloudId;
	}

	public String getHasLyrics() {
		return hasLyrics;
	}

	public void setHasLyrics(String hasLyrics) {
		this.hasLyrics = hasLyrics;
	}


	public Lyrics getLyrics() {
		return lyrics;
	}

	public void setLyrics(Lyrics lyrics) {
		this.lyrics = lyrics;
	}

}
