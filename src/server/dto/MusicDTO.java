package server.dto;

import interfaces.MFLyrics;
import interfaces.MFMusic;

public class MusicDTO implements MFMusic{

	private String trackId;
	private String trackName;
	private String idArtist;
	private String artistName;
	private String albumId;
	private String spotifyId;
	private String soundCloudId;
	private String hasLyrics;
	private MFLyrics lyrics;
	
	public MusicDTO(String trackId, String trackName, String idArtist, String artistName,
				  String albumId, String spotifyId, String soundCloudId, String hasLyrics,
				  MFLyrics lyrics){
		this.trackId=trackId;
		this.trackName=trackName;
		this.idArtist=idArtist;
		this.artistName=artistName;
		this.albumId=albumId;
		this.spotifyId=spotifyId;
		this.soundCloudId=soundCloudId;
		this.hasLyrics=hasLyrics;
		this.lyrics=lyrics;
	}
	
	/**
	 * Utiliser surtout dans la récupération des musique correspond aux critéres par conséquent
	 * on a pas bessoin de savoir si une musique a des lyrics (si on la trouvé c'est qu'elle a forcément 
	 * des paroles, on a pas bessoin de savoir les paroles de la musique non plus
	 */
	public MusicDTO(String trackId, String trackName, String idArtist, String artistName,
			  String albumId, String spotifyId, String soundCloudId){ 
	this.trackId=trackId;
	this.trackName=trackName;
	this.idArtist=idArtist;
	this.artistName=artistName;
	this.albumId=albumId;
	this.spotifyId=spotifyId;
	this.soundCloudId=soundCloudId;
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
	public MFLyrics getLyrics() {
		return lyrics;
	}

	@Override
	public void setLyrics(MFLyrics lyrics) {
		this.lyrics=lyrics;
	}

}
