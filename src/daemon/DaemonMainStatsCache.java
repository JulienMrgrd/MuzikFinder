package daemon;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import server.services.MuzikFinderService;

/**
 * ATTENTION : Renommer cette classe ou le package doit entraîner une modification de la tâche "SCHEDULER" sur Heroku
 * @author MoussaN
 * Cette classe s'occupe du remplissage de la collection STATS_CACHE afin de pouvoir optimiser le traitement 
 * de l'affichage des tops musiques. L'idée est que le deamon devra, toutes les heures, mettre à jour les 
 * statistiques des tops musiques pour que, lors d'une demande de l'utilisateur pour cette information,
 * on n'aura pas à tout calculer. Il aura juste à récupérer ce qui est demandé dans cette classe et ainsi
 * fournir à l'utilisateur une réponse clair et rapide. On se limite à toutes les heures par souci de ne dépasser
 * le nombre de requêtes imposés par notre hébérgeur.
 */

public class DaemonMainStatsCache {
	private MuzikFinderService service;
	
	public DaemonMainStatsCache() {
		service = MuzikFinderService.getInstance(false);
	}
	
	public void process() {
		System.out.println("===========> Daemon de remplissage de Mongo : Collection Stats_Cache <============");
		Instant start = Instant.now();
		service.addListIdMusicMostPopularAllRange();
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end)); // prints PT1M3.553S
		System.out.println("=========> Fin du daemon de remplissage de Mongo : Collection Stats_Cache <==========");
	}

	public static void main(String[] args) throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				new DaemonMainStatsCache().process();
			}
		}).start();
	}

}
