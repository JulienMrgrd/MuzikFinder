package api.musixMatch.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import api.musixMatch.metier.Artist;
import api.musixMatch.metier.Lyrics;
import api.musixMatch.metier.Music;
import interfaces.MFArtist;
import interfaces.MFLyrics;
import interfaces.MFMusic;

public final class MusixMatchAPIHelper {

	private MusixMatchAPIHelper() {
		// nothing
	}
	
	public static JSONObject getBody(String json){
		JSONObject root = new JSONObject(json);
		JSONObject message = root.getJSONObject("message");
		return message.getJSONObject("body");
	}
	
	public static List<MFArtist> getArtistsList(String json){
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MusixMatchConstants.ARTIST_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		List<MFArtist> artists = null;
		try {
			
			Gson gson = new Gson();
			JSONArray jsonList = root.getJSONArray(MusixMatchConstants.ARTIST_LIST);
			artists = new ArrayList<>(jsonList.length());
			JSONObject tmpObj;
			for(int i=0; i<jsonList.length(); i++){
				tmpObj = jsonList.getJSONObject(i).getJSONObject(MusixMatchConstants.ARTIST);
				artists.add( gson.fromJson( tmpObj.toString() , Artist.class) );
			}
			
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return artists;
	}

	public static List<MFMusic> getMusicsList(String json) {
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MusixMatchConstants.TRACK_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		List<MFMusic> musics = null;
		try {
			
			Gson gson = new Gson();
			JSONArray jsonList = root.getJSONArray(MusixMatchConstants.TRACK_LIST);
			musics = new ArrayList<>(jsonList.length());
			JSONObject tmpObj;
			for(int i=0; i<jsonList.length(); i++){
				tmpObj = jsonList.getJSONObject(i).getJSONObject(MusixMatchConstants.TRACK);
				musics.add( gson.fromJson( tmpObj.toString() , Music.class) );
			}
			
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return musics;
	}

	public static MFLyrics getLyrics(String json) {
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MusixMatchConstants.LYRICS); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		try {
			Gson gson = new Gson();
			JSONObject jsonLyrics = root.getJSONObject(MusixMatchConstants.LYRICS);
			return gson.fromJson( jsonLyrics.toString() , Lyrics.class);
		} catch (JSONException e){
			e.printStackTrace();
		}
		return null;
	}

}
