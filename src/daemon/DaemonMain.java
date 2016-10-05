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
//		service.getTracksFromAPI(NB_TRACKS_PROCESS);
		
		MongoService mongo = new MongoService();
		System.out.println("init ok");
		DBCollection collection = mongo.getCollection("songs");
		System.out.println("get collection ok");
		List<BasicDBObject> docs = mongo.createFakeBasicDBObjects();
		mongo.insertMany(collection, docs);

		BasicDBObject before = new BasicDBObject("song", "One Sweet Day");
		BasicDBObject after = new BasicDBObject("$set", new BasicDBObject("artist", "Mariah Carey ft. Boyz II Men"));
		mongo.updateOne(collection, before, after);

		BasicDBObject findQuery = new BasicDBObject("weeksAtOne", new BasicDBObject("$gte",10));
		BasicDBObject orderBy = new BasicDBObject("decade", 1);
		DBCursor cursor = mongo.findBy(collection, findQuery, orderBy);

		while(cursor.hasNext()){
			DBObject doc = cursor.next();
			System.out.println(
					"In the " + doc.get("decade") + ", " + doc.get("song") + 
					" by " + doc.get("artist") + " topped the charts for " + 
					doc.get("weeksAtOne") + " straight weeks."
					);
		}

		mongo.dropCollection(collection);
		System.out.println("drop ok");
		mongo.close();
		
		System.out.println("=========> Fin du daemon de remplissage de Mongo <==========");
	}

	public static void main(String[] args) throws IOException {
		new DaemonMain().process();
	}
}
