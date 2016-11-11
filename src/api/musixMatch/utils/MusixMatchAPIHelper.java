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

	private MusixMatchAPIHelper() {}
	
	private static JSONObject getBody(String json){
		if(json==null) return null;
		JSONObject root = new JSONObject(json);
		JSONObject message = root.getJSONObject("message");
		
		if(message==null || !message.has("body")) return null;
		Object obj = message.get("body");
		if(obj instanceof JSONObject) return message.getJSONObject("body");
		return null;
	}
	
	public static List<MFArtist> getArtistsList(String json){
		if(json==null) return null;
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MusixMatchConstants.ARTIST_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		if(root==null) return null;
		
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
		if(json==null) return null;
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MusixMatchConstants.TRACK_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		if(root==null) return null;
		
		List<MFMusic> musics = null;
		try {
			
			Gson gson = new Gson();
			JSONArray jsonList = root.getJSONArray(MusixMatchConstants.TRACK_LIST);
			musics = new ArrayList<>(jsonList.length());
			JSONObject tmpObj;
			MFMusic music;
			for(int i=0; i<jsonList.length(); i++){
				tmpObj = jsonList.getJSONObject(i).getJSONObject(MusixMatchConstants.TRACK);
				music = gson.fromJson(tmpObj.toString(), Music.class);
				String genre = getFirstGenre(tmpObj);
				if(genre != null) music.setMusicGenre(genre);

				musics.add( music );
			}
			
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return musics;
	}
	
	private static String getFirstGenre(JSONObject jsonSong) {
		try {
			JSONObject primary = jsonSong.getJSONObject("primary_genres");
			JSONArray array = primary.getJSONArray("music_genre_list");
			JSONObject first = array.getJSONObject(0).getJSONObject("music_genre");
			if(first.getString("music_genre_name")!=null) return first.getString("music_genre_name");
		} catch (Exception e){}
		
		try {
			JSONObject primary = jsonSong.getJSONObject("secondary_genres");
			JSONArray array = primary.getJSONArray("music_genre_list");
			JSONObject first = array.getJSONObject(0).getJSONObject("music_genre");
			if(first.getString("music_genre_name")!=null) return first.getString("music_genre_name");
		} catch (Exception e){}
		return null;
	}

	public static MFLyrics getLyrics(String json) {
		if(json==null) return null;
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MusixMatchConstants.LYRICS); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		if(root==null) return null;
		
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
