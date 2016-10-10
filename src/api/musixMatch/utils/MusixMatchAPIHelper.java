package api.musixMatch.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import api.musixMatch.metier.Album;
import api.musixMatch.metier.Artist;
import api.musixMatch.metier.Lyrics;
import api.musixMatch.metier.Music;
import interfaces.MFArtist;
import interfaces.MFLyrics;
import interfaces.MFMusic;
import utils.MuzikFinderConstants;

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
			root.getJSONObject(MuzikFinderConstants.ARTIST_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		List<MFArtist> artists = null;
		try {
			
			Gson gson = new Gson();
			JSONArray jsonList = root.getJSONArray(MuzikFinderConstants.ARTIST_LIST);
			artists = new ArrayList<>(jsonList.length());
			JSONObject tmpObj;
			for(int i=0; i<jsonList.length(); i++){
				tmpObj = jsonList.getJSONObject(i).getJSONObject(MuzikFinderConstants.ARTIST);
				artists.add( gson.fromJson( tmpObj.toString() , Artist.class) );
			}
			
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return artists;
	}

	public static List<Album> getAlbumList(String json) {
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MuzikFinderConstants.ALBUM_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		List<Album> albums = null;
		try {
			
			Gson gson = new Gson();
			JSONArray jsonList = root.getJSONArray(MuzikFinderConstants.ALBUM_LIST);
			albums = new ArrayList<>(jsonList.length());
			JSONObject tmpObj;
			for(int i=0; i<jsonList.length(); i++){
				tmpObj = jsonList.getJSONObject(i).getJSONObject(MuzikFinderConstants.ALBUM);
				albums.add( gson.fromJson( tmpObj.toString() , Album.class) );
			}
			
		} catch (JSONException e){
			e.printStackTrace();
		}
		
		return albums;
	}

	public static List<MFMusic> getMusicsList(String json) {
		JSONObject root = new JSONObject(json);
		try{
			root.getJSONObject(MuzikFinderConstants.TRACK_LIST); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		List<MFMusic> musics = null;
		try {
			
			Gson gson = new Gson();
			JSONArray jsonList = root.getJSONArray(MuzikFinderConstants.TRACK_LIST);
			musics = new ArrayList<>(jsonList.length());
			JSONObject tmpObj;
			for(int i=0; i<jsonList.length(); i++){
				tmpObj = jsonList.getJSONObject(i).getJSONObject(MuzikFinderConstants.TRACK);
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
			root.getJSONObject(MuzikFinderConstants.LYRICS); // Can throw Exception if key not exists
		} catch (JSONException e){
			root = getBody(json);
		}
		
		try {
			
			Gson gson = new Gson();
			JSONObject jsonLyrics = root.getJSONObject(MuzikFinderConstants.LYRICS);
			return gson.fromJson( jsonLyrics.toString() , Lyrics.class);

		} catch (JSONException e){
			e.printStackTrace();
		}
		return null;
	}

}
