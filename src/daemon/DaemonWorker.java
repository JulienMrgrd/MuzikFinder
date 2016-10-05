package daemon;

import java.util.List;

import nosql.mongo.MongoService;
import sources.mongo.com.mongodb.BasicDBObject;
import sources.mongo.com.mongodb.DBCollection;
import sources.mongo.com.mongodb.DBCursor;
import sources.mongo.com.mongodb.DBObject;

public class DaemonWorker {

	public DaemonWorker() {
		// TODO Auto-generated constructor stub
	}
	
	public void process() {
		System.out.println("===========> Daemon de remplissage de Mongo <============");
		MongoService mongo = new MongoService();
		System.out.println("mongoservice OK");
		DBCollection collection = mongo.getCollection("songs");
		System.out.println("mongo collection OK");
		
		List<BasicDBObject> docs = mongo.createFakeBasicDBObjects();
		mongo.insertMany(collection, docs);
		System.out.println("mongo insert many OK");
		
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

		mongo.close();
		System.out.println("=========> Fin du daemon de remplissage de Mongo <==========");
	}

}
