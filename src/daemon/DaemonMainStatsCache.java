package daemon;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import server.services.MuzikFinderService;
import utils.MuzikFinderPreferences;

/**
 * ATTENTION : Renommer cette classe ou le package doit entraîner une modification de la tâche "SCHEDULER" correspondante sur Heroku
 *
 * Cette classe s'occupe du remplissage de la collection STATS_CACHE afin de pouvoir optimiser le traitement 
 * de l'affichage des tops musiques. Le daemon devra, toutes les heures, mettre à jour les 
 * statistiques des tops musiques pour que, lors d'une demande de l'utilisateur pour cette information,
 * aucun gros traitements de parcours d'information n'ai lieu. 
 * Il aura juste à récupérer ce qui est demandé dans cette collection et ainsi fournir à l'utilisateur 
 * une réponse rapide.
 */

public class DaemonMainStatsCache {
	private MuzikFinderService service;
	
	public DaemonMainStatsCache() {
		service = MuzikFinderService.getInstance(false);
	}
	
	public void process() {
		System.out.println("=======> Fin du daemon du remplissage des tops et suppression du cache des recherches <=======");
		Instant start = Instant.now();
		
		service.addListIdMusicMostPopularAllRanges();
		service.deleteCacheUserExceed(MuzikFinderPreferences.getTimeTopCache());

		Instant end = Instant.now();
		System.out.println(Duration.between(start, end)); // prints PT1M3.553S
		System.out.println("=======> Fin du daemon du remplissage des tops et suppression du cache des recherches  <========");
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
