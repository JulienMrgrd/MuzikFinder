package server.musicdto;

import interfaces.MFLyrics;
import interfaces.MFMusic;

public class MusicDTO implements MFMusic{

	private String trackName;
	private String artistName;
	private String spotifyId;
	private String soundCloudId;
	
	public MusicDTO(String trackName, String artistName, String spotifyId, String soundCloudId){
		this.trackName=trackName;
		this.artistName=artistName;
		this.spotifyId=spotifyId;
		this.soundCloudId=soundCloudId;
	}

	@Override
	public String getTrackId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTrackId(String trackId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTrackName() {
		return trackName;
	}

	@Override
	public void setTrackName(String trackName) {
		
	}

	@Override
	public String getArtistId() {
		return null;
	}

	@Override
	public void setArtistId(String artistId) {
		
	}

	@Override
	public String getArtistName() {
		return artistName;
	}

	@Override
	public void setArtistName(String artistName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAlbumId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAlbumId(String albumId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTrackSpotifyId() {
		return spotifyId;
	}

	@Override
	public void setTrackSpotifyId(String trackSpotifyId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTrackSoundcloudId() {
		return soundCloudId;
	}

	@Override
	public void setTrackSoundcloudId(String trackSoundcloudId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHasLyrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHasLyrics(String hasLyrics) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MFLyrics getLyrics() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLyrics(MFLyrics lyrics) {
		// TODO Auto-generated method stub
		
	}

}
