package server.dto;

import interfaces.MFArtist;

public class ArtistDTO implements MFArtist{

	private String artistId;
	private String artistName;
	
	public ArtistDTO(String artistId, String artistName){
		this.artistId = artistId;
		this.artistName = artistName;
	}
	
	@Override
	public String getArtistId() {
		return artistId;
	}

	@Override
	public void setArtistId(String artistId) {
		this.artistId=artistId;
	}

	@Override
	public String getArtistName() {
		return artistName;
	}

	@Override
	public void setArtistName(String artistName) {
		this.artistName=artistName;
	}

}
