package server.dto;

import interfaces.MFAlbum;

public class AlbumDTO implements MFAlbum{

	private String albumId;
	private String albumName;
	private String albumTrackCount;
	private String albumReleaseDate;
	private String albumReleaseType;
	private String artistName;

	public AlbumDTO(String albumId, String albumName, String albumTrackCount,
					String albumReleaseDate, String albumReleaseType,
					String artistName){
		this.albumId = albumId;
		this.albumName = albumName;
		this.albumTrackCount = albumTrackCount;
		this.albumReleaseDate = albumReleaseDate;
		this.albumReleaseType = albumReleaseType;
		this.artistName = artistName;
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
		this.albumName=albumName;
	}

	@Override
	public String getAlbumTrackCount() {
		return albumTrackCount;
	}

	@Override
	public void setAlbumTrackCount(String albumTrackCount) {
		this.albumTrackCount=albumTrackCount;
	}

	@Override
	public String getAlbumReleaseDate() {
		return albumReleaseDate;
	}

	@Override
	public void setAlbumReleaseDate(String albumReleaseDate) {
		this.albumReleaseDate=albumReleaseDate;
	}

	@Override
	public String getAlbumReleaseType() {
		return albumReleaseType;
	}

	@Override
	public void setAlbumReleaseType(String albumReleaseType) {
		this.albumReleaseType=albumReleaseType;
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
