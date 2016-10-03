package musixMatch;

import java.io.IOException;
import java.util.List;

import musixMatch.requete.Requete;

public class MainTest {

	public static void main(String[] args) throws IOException {
		//15445219
		List<String> listArtiste;
		
		//On recupere les 5 meilleurs artiste aux US
		listArtiste=Requete.getChartArtist();
		List<String> listAlbum;
		List<String> listIdTrack;
		
		//for(int i=0; i<listArtiste.size();i++){
		for(int i=0; i<2;i++){ //Pour tester et pas niquer tous le quota
			System.out.println("Artiste n°"+i+" = "+listArtiste.get(i));
			
			//On recupere les 3 albums les plus recents de chaque artiste
			listAlbum=Requete.getAlbum(listArtiste.get(i));
			
			for(int j=0;j<listAlbum.size();j++){
				
				//recupere les id de chaque musique de l'album
				listIdTrack=Requete.getTrackAlbum(listAlbum.get(j));
				
				//recupere les paroles de la 1ere musique de l'album
				Requete.getTrackLyric(listIdTrack.get(0));
			}
			
		}

	}

}
