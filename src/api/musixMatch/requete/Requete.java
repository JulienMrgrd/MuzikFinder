package api.musixMatch.requete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.musixMatch.utils.Constant;

public class Requete {

	/**
	 * retourne les paroles d'un musique a partir de son id
	 * @param id id de la musique sur musixMatch
	 * @throws IOException 
	 */
	public static String getTrackLyric(String id) throws IOException{
		String urlString = Constant.API_URL;
		urlString+="track.lyrics.get?";
		urlString+="apikey="+Constant.API_KEY;
		urlString+="&track_id="+id;
		String tmp = "";
		System.out.println(urlString);

		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			//System.out.println(inputLine);
			String s = inputLine.split("lyrics_body")[1];
			tmp = s.split("This Lyrics is NOT for Commercial use")[0];
			//System.out.println(tmp);
			tmp=tmp.substring(3, tmp.length()-8);
			//System.out.println(tmp);
		}
		in.close();

		return tmp;
	}

	/**
	 * @param id id de la musique sur musixMatch
	 * @throws IOException 
	 */
	public static void getSpotifyId(String id)throws IOException{
		String urlString = Constant.API_URL;
		urlString+="track.get?";
		urlString+="apikey="+Constant.API_KEY;
		urlString+="&track_id="+id;

		System.out.println(urlString);

		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
			String s = inputLine.split("track_spotify_id")[1];
			String tmp = s.split("track_soundcloud_id")[0];
			System.out.println(tmp);
			tmp=tmp.substring(3, tmp.length()-3);
			System.out.println(tmp);
		}
		in.close();
	}

	/**
	 * retourne la liste des 5 meilleur artiste au US (pour l'instant)
	 * @return liste des id des 5 meilleurs artistes au US
	 * @throws IOException
	 */
	public static List<String> getChartArtist()throws IOException{
		String urlString = Constant.API_URL;
		urlString+="chart.artists.get?";
		urlString+="apikey="+Constant.API_KEY;
		urlString+="&page=1&page_size=5&country=us";

		List<String> listeArtiste = new ArrayList<String>();
		System.out.println(urlString);

		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;
		int classement=0;
		String tmp;
		while ((inputLine = in.readLine()) != null) {
			String id[] = inputLine.split("artist_id");
			for(int j=1;j<id.length; j++){
				tmp=id[j].split(",")[0].substring(2);
				//System.out.println(tmp);
				listeArtiste.add(tmp);
				//System.out.println("j = "+j);
				//trackSearch(tmp,j);
			}
			/*String s[] = inputLine.split("artist_name");
	    	for(int i=1; i<s.length; i=i+1){
	    		String tmp = s[i].split(",")[0];
	    		if(!tmp.startsWith("_translation")){
	    			tmp= tmp.substring(3, tmp.length()-1);
	    			classement++;
	    			System.out.println("n�"+classement+"="+tmp);
	    		}
	    	}*/
			/*System.out.println(tmp);
	    	tmp=tmp.substring(3, tmp.length()-3);
	        System.out.println(tmp);*/
		}
		in.close();

		return listeArtiste;
	}


	/**
	 * retourne les albums d'un artiste a partir de son id (limite a 3 pour l'instant)
	 * @param id id de l'artiste
	 * @throws IOException
	 */
	public static List<String> getAlbum(String id)throws IOException{
		String urlString = Constant.API_URL;
		urlString+="artist.albums.get?";
		urlString+="apikey="+Constant.API_KEY;
		urlString+="&artist_id="+id;
		urlString+="&s_release_date=desc&page_size=3";

		//System.out.println(urlString);

		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;
		List<String> listIdAlbum=new ArrayList<String>();

		while ((inputLine = in.readLine()) != null) {
			String s[] = inputLine.split("album_id");
			for(int i=1;i<s.length;i++){
				String tmp=s[i].split("album")[0];
				if(tmp!=null){
					tmp=tmp.substring(2, tmp.length()-2);
					System.out.println("Album n�"+i+" = "+tmp);
					listIdAlbum.add(tmp);
				}
			}
		}
		in.close();
		return listIdAlbum;
	}

	/**
	 * retourne les musique de l'album a partir de l'id de l'album
	 * @param id id de l'album
	 * @throws IOException
	 */
	public static List<String> getTrackAlbum(String id) throws IOException{
		String urlString = Constant.API_URL;
		urlString+="album.tracks.get?";
		urlString+="apikey="+Constant.API_KEY;
		urlString+="&album_id="+id+"&page_size=20";

		System.out.println(urlString);

		List<String> listIdTrack=new ArrayList<String>();

		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			//System.out.println(inputLine);
			String s[]=inputLine.split("\"track_id");
			for(int i=1;i<s.length;i++){
				String tmp=s[i].split("track_mbid")[0];
				if(tmp!=null){
					tmp=tmp.substring(2, tmp.length()-2);
					listIdTrack.add(tmp);
				}
			}
		}
		in.close();
		return listIdTrack;
	}

	/**
	 * retourne le tire de la musique et le nom de l'artiste
	 * @param id
	 * @return
	 * @throws IOException
	 */
	public static String getTrackNameAndArtist(String id) throws IOException{
		String urlString = Constant.API_URL;
		urlString+="track.get?";
		urlString+="apikey="+Constant.API_KEY;
		urlString+="&track_id="+id;
		String tmp = "";

		System.out.println();
		URL url = new URL(urlString);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			//System.out.println(inputLine);
			String s = inputLine.split("track_name")[1].substring(3);
			String nameTrack = s.split(",")[0];
			nameTrack=nameTrack.substring(0, nameTrack.length()-1);

			s = inputLine.split("artist_name")[1].substring(3);
			String artiste = s.split(",")[0];
			artiste=artiste.substring(0, artiste.length()-1);
			//System.out.println(tmp);
			tmp=nameTrack+" ___ "+artiste;
			tmp=tmp.replace("\u00e9", "e");
			System.out.println(tmp);
		}
		in.close();

		return tmp;
	}

	/**
	 * 
	 * @param requete
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	public static Map<String,String> getInfoTrack(String id) throws IOException{
		
		String requete = Constant.API_URL;
		requete+="track.get?";
		requete+="apikey="+Constant.API_KEY;
		requete+="&track_id="+id;
		
		URL url = new URL(requete);
		URLConnection uc = url.openConnection();

		InputStreamReader input = new InputStreamReader(uc.getInputStream());
		BufferedReader in = new BufferedReader(input);
		String inputLine;
		
		inputLine=in.readLine();
		System.out.println(inputLine);
		Map<String, String> infoTrack= new HashMap<>();
		
		//get Spotify id
		String s = inputLine.split("track_spotify_id")[1];
		String tmp = s.split("track_soundcloud_id")[0];
		tmp=tmp.substring(3, tmp.length()-3);
		
		infoTrack.put("spotify_id", tmp);
		
		//get soundcloud id
		s = inputLine.split("track_soundcloud_id")[1];
		tmp = s.split("track_xboxmusic_id")[0];
		if(tmp.length()>6) tmp=tmp.substring(2, tmp.length()-2);
		
		infoTrack.put("soundcloud_id", tmp);
		
		//get Artist Name
		s = inputLine.split("artist_name")[1];
		String artiste = s.split(",")[0];
		artiste=artiste.substring(3, artiste.length()-1);
		
		infoTrack.put("artist", artiste);
		
		//get track name
		s = inputLine.split("track_name")[1];
		String nameTrack = s.split(",")[0];
		nameTrack=nameTrack.substring(3, nameTrack.length()-1);
		
		infoTrack.put("trackname", nameTrack);
		
		return infoTrack;
	}
}