package daemon;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import server.services.MuzikFinderService;

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
