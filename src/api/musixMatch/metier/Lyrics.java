package api.musixMatch.metier;

import com.google.gson.annotations.SerializedName;

import interfaces.MFLyrics;

public class Lyrics implements MFLyrics{
	
	@SerializedName("lyrics_body")
	private String lyricsBody;
	
	@SerializedName("lyrics_language")
	private String lyrics_language;

	public String getLyricsBody() {
		return lyricsBody;
	}

	public void setLyricsBody(String lyricsBody) {
		this.lyricsBody = lyricsBody;
	}

	public String getLyrics_language() {
		return lyrics_language;
	}

	public void setLyrics_language(String lyrics_language) {
		this.lyrics_language = lyrics_language;
	}

	
}
