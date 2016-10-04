package server.metier;

public class Music {

	private String idMusic;
	private String name;
	private String artistName;
	private String artistId;
	private String lyrics;
	private String spotifyLink;

	public Music() {
	}

	public Music(String idMusic, String name, String artistName, String artistId, String lyrics, String spotifyLink) {
		super();
		this.idMusic = idMusic;
		this.name = name;
		this.artistName = artistName;
		this.artistId = artistId;
		this.lyrics = lyrics;
		this.spotifyLink = spotifyLink;
	}

	public String getIdMusic() {
		return idMusic;
	}

	public void setIdMusic(String idMusic) {
		this.idMusic = idMusic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public String getArtistId() {
		return artistId;
	}

	public void setArtistId(String artistId) {
		this.artistId = artistId;
	}

	public String getLyrics() {
		return lyrics;
	}

	public void setLyrics(String lyrics) {
		this.lyrics = lyrics;
	}

	public String getSpotifyLink() {
		return spotifyLink;
	}

	public void setSpotifyLink(String spotifyLink) {
		this.spotifyLink = spotifyLink;
	}

}
