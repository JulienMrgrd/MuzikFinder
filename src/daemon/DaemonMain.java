package daemon;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import api.musixMatch.utils.RequestHelper;
import server.services.MuzikFinderService;

/**
 * ATTENTION : Renommer cette classe ou le package doit entraîner une modification de la tâche "SCHEDULER" sur Heroku
 * @author JulienM
 * Cette classe s'occupe du remplissage de la base NOSQL avec les données de l'API de musiques.
 */
public class DaemonMain {
	
	private MuzikFinderService service;
	
	public DaemonMain() {
		service = new MuzikFinderService();
	}
	
	public void process() {
		System.out.println("===========> Daemon de remplissage de Mongo <============");
		Instant start = Instant.now();
		service.startFilingDatabaseProcess();
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end)); // prints PT1M3.553S
		System.out.println("Nombre de requêtes émises : "+RequestHelper.cpt);
		System.out.println("=========> Fin du daemon de remplissage de Mongo <==========");
	}

	public static void main(String[] args) throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				new DaemonMain().process();
			}
		}).start();
	}
	
	/*	Exemple de récupération de toutes les musiques d'un artiste
	 	int pos = service.getLastPositionInNoSQL();
		
		System.out.println("Récupération des "+NB_ARTISTS_TO_GET+" top artiste(s)...");
		List<Artist> artists = service.getTopArtistsFromAPI(pos, DaemonMain.NB_ARTISTS_TO_GET, COUNTRY_ORDER[0]);

		System.out.println("\nRécupération des albums de "+artists.get(0).getArtistName()+"...");
		List<String> albumIds = new ArrayList<>();
		artists.forEach(art -> albumIds.addAll(service.getAllAlbumIdsFromAPI(art.getArtistId())) );
		System.out.println(albumIds.size()+" albums récupérées.\n");
		
		Map<String, List<Track>> lyrics = new HashMap<>(albumIds.size());
		for(String id : albumIds){
			System.out.println("Récupération des musiques de l'album "+id+"...");
			lyrics.put(id, service.getAllTracksFromAPI(id));
			System.out.println(lyrics.get(id).size()+" musiques récupérées.");
		}
		
	 */
}
