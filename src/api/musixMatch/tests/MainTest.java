package api.musixMatch.tests;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.musixMatch.requete.Requete;

public class MainTest {

	public static void main(String[] args) throws IOException {
		//15445219
		List<String> listArtiste;
		
		Map<String,String> infoTrack=new HashMap<String,String>();
		
		//System.setProperty("http.proxyHost", "proxy");
		//System.setProperty("http.proxyPort", "3128");
		
		//On recupere les 5 meilleurs artiste aux US
		listArtiste=Requete.getChartArtist();
		List<String> listAlbum;
		List<String> listIdTrack;
		
		//for(int i=0; i<listArtiste.size();i++){
		for(int i=0; i<2;i++){ //Pour tester et pas niquer tous le quota
			System.out.println("Artiste n°"+i+" = "+listArtiste.get(i));
			
			//On recupere les 3 albums les plus recents de chaque artiste
			listAlbum=Requete.getAlbum(listArtiste.get(i));
			
			//System.out.println(listAlbum.size());
			for(int j=0;j<listAlbum.size();j++){
				
				//recupere les id de chaque musique de l'album
				listIdTrack=Requete.getTrackAlbum(listAlbum.get(j));
				System.out.println("listIdTrack size = "+listIdTrack.size());
				
				//recupere les paroles de la 1ere musique de l'album
				infoTrack=Requete.getInfoTrack(listIdTrack.get(0));
				
				System.out.println("artiste name = "+infoTrack.get("artist"));
				System.out.println("track name = "+infoTrack.get("trackname"));
				//System.out.println("lyrics = "+infoTrack.get("lyrics"));
				System.out.println("spotify_id = "+infoTrack.get("spotify_id"));
				System.out.println("soundcloud_id = "+infoTrack.get("soundcloud_id"));
				System.out.println("parole = "+Requete.getTrackLyric(listIdTrack.get(0)));/*t*/
			}
			
		}

	}

}
