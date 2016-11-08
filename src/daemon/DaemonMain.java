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
		service = new MuzikFinderService(false);
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
	
}
