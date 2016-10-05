package daemon;

import java.io.IOException;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import nosql.mongo.MongoService;
import server.services.MuzikFinderService;

/**
 * ATTENTION : Renommer cette classe ou le package doit entraîner une modification de la tâche "SCHEDULER" sur Heroku
 * @author JulienM
 * Cette classe s'occupe du remplissage de la base NOSQL avec les données de l'API de musiques.
 */
public class DaemonMain {
	
	private static final int NB_TRACKS_PROCESS = 20;
	private MuzikFinderService service;
	
	public DaemonMain() {
		service = new MuzikFinderService();
	}
	
	public void process() {
		System.out.println("===========> Daemon de remplissage de Mongo <============");
		service.getTracksFromAPI(NB_TRACKS_PROCESS);
		service.startMongo();
		System.out.println("=========> Fin du daemon de remplissage de Mongo <==========");
	}

	public static void main(String[] args) throws IOException {
		new DaemonMain().process();
	}
}
