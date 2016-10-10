package api.musixMatch.metier;

import com.google.gson.annotations.SerializedName;

public class Album {

	@SerializedName("album_id")
    private String albumId;
	
	@SerializedName("album_name")
    private String albumName;
	
	@SerializedName("album_track_count")
    private String albumTrackCount;
	
	@SerializedName("album_release_date")
    private String albumReleaseDate;
	
	@SerializedName("album_release_type")
    private String albumReleaseType;
	
	@SerializedName("artist_name")
    private String artistName;

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

	public String getAlbumTrackCount() {
		return albumTrackCount;
	}

	public void setAlbumTrackCount(String albumTrackCount) {
		this.albumTrackCount = albumTrackCount;
	}

	public String getAlbumReleaseDate() {
		return albumReleaseDate;
	}

	public void setAlbumReleaseDate(String albumReleaseDate) {
		this.albumReleaseDate = albumReleaseDate;
	}

	public String getAlbumReleaseType() {
		return albumReleaseType;
	}

	public void setAlbumReleaseType(String albumReleaseType) {
		this.albumReleaseType = albumReleaseType;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

}
