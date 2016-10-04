package daemon;

import java.io.IOException;

/**
 * ATTENTION : Renommer cette classe ou le package doit entraîner une modification de la tâche "SCHEDULER" sur Heroku
 * @author JulienM
 * Cette classe s'occupe du remplissage de la base NOSQL avec les données de l'API de musiques.
 */
public class DaemonMain {

	public static void main(String[] args) throws IOException {
		System.out.println("===========> Tache cron <============");
//		//15445219
//		List<String> listArtiste;
//
//		//On recupere les 5 meilleurs artiste aux US
//		listArtiste=Requete.getChartArtist();
//		List<String> listAlbum;
//		List<String> listIdTrack;
//
//		//for(int i=0; i<listArtiste.size();i++){
//		for(int i=0; i<2;i++){ //Pour tester et pas niquer tous le quota
//			System.out.println("Artiste n°"+i+" = "+listArtiste.get(i));
//
//			//On recupere les 3 albums les plus recents de chaque artiste
//			listAlbum=Requete.getAlbum(listArtiste.get(i));
//
//			for(int j=0;j<listAlbum.size();j++){
//
//				//recupere les id de chaque musique de l'album
//				listIdTrack=Requete.getTrackAlbum(listAlbum.get(j));
//
//				//recupere les paroles de la 1ere musique de l'album
//				Requete.getTrackLyric(listIdTrack.get(0));
//			}
//
//		}
	}

}
