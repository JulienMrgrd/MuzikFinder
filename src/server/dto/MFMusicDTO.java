package server.dto;

import interfaces.MFLyrics;
import interfaces.MFMusic;

public class MFMusicDTO implements MFMusic{

	private String trackId;
	private String trackName;
	private String idArtist;
	private String artistName;
	private String albumId;
	private String albumName;
	private String spotifyId;
	private String soundCloudId;
	private String hasLyrics;
	private MFLyricsDTO lyrics;
	private String musicGenre;

	public MFMusicDTO(String trackId, String trackName, String idArtist, String artistName,
			String albumId, String albumName, String spotifyId, String soundCloudId, String hasLyrics,
			MFLyricsDTO lyrics, String musicGenre){
		this.trackId=trackId;
		this.trackName=trackName;
		this.idArtist=idArtist;
		this.artistName=artistName;
		this.albumId=albumId;
		this.albumName=albumName;
		this.spotifyId=spotifyId;
		this.soundCloudId=soundCloudId;
		this.hasLyrics=hasLyrics;
		this.lyrics=lyrics;
		this.musicGenre=musicGenre;
	}

	@Override
	public String getTrackId() {
		return trackId;
	}

	@Override
	public void setTrackId(String trackId) {
		this.trackId=trackId;
	}

	@Override
	public String getTrackName() {
		return trackName;
	}

	@Override
	public void setTrackName(String trackName) {
		this.trackName=trackName;
	}

	@Override
	public String getArtistId() {
		return idArtist;
	}

	@Override
	public void setArtistId(String artistId) {
		this.idArtist=artistId;
	}

	@Override
	public String getArtistName() {
		return artistName;
	}

	@Override
	public void setArtistName(String artistName) {
		this.artistName=artistName;
	}

	@Override
	public String getAlbumId() {
		return albumId;
	}

	@Override
	public void setAlbumId(String albumId) {
		this.albumId=albumId;
	}

	@Override
	public String getAlbumName() {
		return albumName;
	}

	@Override
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	@Override
	public String getTrackSpotifyId() {
		return spotifyId;
	}

	@Override
	public void setTrackSpotifyId(String trackSpotifyId) {
		this.spotifyId=trackSpotifyId;
	}

	@Override
	public String getTrackSoundcloudId() {
		return soundCloudId;
	}

	@Override
	public void setTrackSoundcloudId(String trackSoundcloudId) {
		this.soundCloudId=trackSoundcloudId;
	}

	@Override
	public String getHasLyrics() {
		return hasLyrics;
	}

	@Override
	public void setHasLyrics(String hasLyrics) {
		this.hasLyrics=hasLyrics;
	}

	@Override
	public MFLyricsDTO getLyrics() {
		return lyrics;
	}

	@Override
	public void setLyrics(MFLyrics lyrics) {
		this.lyrics=(MFLyricsDTO) lyrics;
	}

	@Override
	public String getMusicGenre() {
		return musicGenre;
	}

	@Override
	public void setMusicGenre(String musicGenre) {
		this.musicGenre = musicGenre;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MFMusicDTO 
				&& this.getArtistName().equals(((MFMusic) obj).getArtistName()) 
				&& this.getTrackName().equals(((MFMusic) obj).getTrackName())){
			return true;
		}
		return false;
	}

	public int hashCode() {
		return 0;
	}

}
