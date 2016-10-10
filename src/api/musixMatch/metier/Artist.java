package api.musixMatch.metier;

import com.google.gson.annotations.SerializedName;

import interfaces.MFArtist;

public class Artist implements MFArtist{

    @SerializedName("artist_id")
    private String artistId;
	
    @SerializedName("artist_name")
    private String artistName;
	
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

}
