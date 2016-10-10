package interfaces;

public interface MFMusic {
	
	public String getTrackId();

	public void setTrackId(String trackId);

	public String getTrackName();

	public void setTrackName(String trackName);

	public String getArtistId();

	public void setArtistId(String artistId);

	public String getArtistName();

	public void setArtistName(String artistName);

	public String getAlbumId();

	public void setAlbumId(String albumId);

	public String getTrackSpotifyId();

	public void setTrackSpotifyId(String trackSpotifyId);

	public String getTrackSoundcloudId();

	public void setTrackSoundcloudId(String trackSoundcloudId);

	public String getHasLyrics();

	public void setHasLyrics(String hasLyrics);

	public MFLyrics getLyrics();

	public void setLyrics(MFLyrics lyrics);

}
