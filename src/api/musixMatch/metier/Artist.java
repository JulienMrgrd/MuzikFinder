package api.musixMatch.metier;

import com.google.gson.annotations.SerializedName;

public class Artist {

    @SerializedName("artist_id")
    private String artistId;
	
    @SerializedName("artist_name")
    private String artistName;
	
    @SerializedName("artist_country")
    private String artistCountry;
	
    @SerializedName("artist_twitter_url")
    private String artistTwitterUrl;

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

	public String getArtistCountry() {
		return artistCountry;
	}

	public void setArtistCountry(String artistCountry) {
		this.artistCountry = artistCountry;
	}

	public String getArtistTwitterUrl() {
		return artistTwitterUrl;
	}

	public void setArtistTwitterUrl(String artistTwitterUrl) {
		this.artistTwitterUrl = artistTwitterUrl;
	}
    
}
