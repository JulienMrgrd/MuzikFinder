package server.dto;

import interfaces.MFLyrics;

public class MFLyricsDTO implements MFLyrics{

	private String lyricsBody;
	private String lyrics_language;
	
	public MFLyricsDTO(String lyricsBody, String lyrics_language){
		this.lyricsBody = lyricsBody;
		this.lyrics_language = lyrics_language;
	}
	
	@Override
	public String getLyricsBody() {
		return lyricsBody;
	}

	@Override
	public void setLyricsBody(String lyricsBody) {
		this.lyricsBody=lyricsBody;
	}

	@Override
	public String getLyrics_language() {
		return lyrics_language;
	}

	@Override
	public void setLyrics_language(String lyrics_language) {
		this.lyrics_language=lyrics_language;
	}

}
