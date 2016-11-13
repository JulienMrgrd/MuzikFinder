package api.musixMatch.metier;

import com.google.gson.annotations.SerializedName;

import interfaces.MFLyrics;
import interfaces.MFMusic;

public class Music implements MFMusic {

	@SerializedName("track_id")
	private String trackId;
	
	@SerializedName("track_name")
	private String trackName;
	
	@SerializedName("artist_id")
	private String artistId;
	
	@SerializedName("artist_name")
	private String artistName;
	
	@SerializedName("album_id")
	private String albumId;
	
	@SerializedName("album_name")
	private String albumName;
	
	@SerializedName("track_spotify_id")
	private String trackSpotifyId;
	
	@SerializedName("track_soundcloud_id")
	private String trackSoundcloudId;
	
	@SerializedName("has_lyrics")
	private String hasLyrics;
	
	private String musicGenre;

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

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getAlbumId() {
		return albumId;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	
	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
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

	public MFLyrics getLyrics() {
		return lyrics;
	}

	public void setLyrics(MFLyrics lyrics) {
		this.lyrics = (Lyrics) lyrics;
	}

	public String getMusicGenre() {
		return musicGenre;
	}

	public void setMusicGenre(String musicGenre) {
		this.musicGenre = musicGenre;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Music other = (Music) obj;
		if (albumId == null) {
			if (other.albumId != null)
				return false;
		} else if (!albumId.equals(other.albumId))
			return false;
		if (albumName == null) {
			if (other.albumName != null)
				return false;
		} else if (!albumName.equals(other.albumName))
			return false;
		if (artistId == null) {
			if (other.artistId != null)
				return false;
		} else if (!artistId.equals(other.artistId))
			return false;
		if (artistName == null) {
			if (other.artistName != null)
				return false;
		} else if (!artistName.equals(other.artistName))
			return false;
		if (hasLyrics == null) {
			if (other.hasLyrics != null)
				return false;
		} else if (!hasLyrics.equals(other.hasLyrics))
			return false;
		if (lyrics == null) {
			if (other.lyrics != null)
				return false;
		} else if (!lyrics.equals(other.lyrics))
			return false;
		if (musicGenre == null) {
			if (other.musicGenre != null)
				return false;
		} else if (!musicGenre.equals(other.musicGenre))
			return false;
		if (trackId == null) {
			if (other.trackId != null)
				return false;
		} else if (!trackId.equals(other.trackId))
			return false;
		if (trackName == null) {
			if (other.trackName != null)
				return false;
		} else if (!trackName.equals(other.trackName))
			return false;
		if (trackSoundcloudId == null) {
			if (other.trackSoundcloudId != null)
				return false;
		} else if (!trackSoundcloudId.equals(other.trackSoundcloudId))
			return false;
		if (trackSpotifyId == null) {
			if (other.trackSpotifyId != null)
				return false;
		} else if (!trackSpotifyId.equals(other.trackSpotifyId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((albumId == null) ? 0 : albumId.hashCode());
		result = prime * result + ((albumName == null) ? 0 : albumName.hashCode());
		result = prime * result + ((artistId == null) ? 0 : artistId.hashCode());
		result = prime * result + ((artistName == null) ? 0 : artistName.hashCode());
		result = prime * result + ((hasLyrics == null) ? 0 : hasLyrics.hashCode());
		result = prime * result + ((lyrics == null) ? 0 : lyrics.hashCode());
		result = prime * result + ((musicGenre == null) ? 0 : musicGenre.hashCode());
		result = prime * result + ((trackId == null) ? 0 : trackId.hashCode());
		result = prime * result + ((trackName == null) ? 0 : trackName.hashCode());
		result = prime * result + ((trackSoundcloudId == null) ? 0 : trackSoundcloudId.hashCode());
		result = prime * result + ((trackSpotifyId == null) ? 0 : trackSpotifyId.hashCode());
		return result;
	}

}
