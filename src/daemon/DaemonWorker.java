package daemon;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import nosql.mongo.MongoService;

public class DaemonWorker {

	public DaemonWorker() {
		// TODO Auto-generated constructor stub
	}
	
	public void process() {
		System.out.println("===========> Daemon de remplissage de Mongo <============");
		MongoService mongo = new MongoService();
		MongoCollection<Document> collection = mongo.getCollection("songs");
		
		List<Document> docs = mongo.createFakeDocuments();
		mongo.insertMany(collection, docs);

		Document before = new Document("song", "One Sweet Day");
		Document after = new Document("$set", new Document("artist", "Mariah Carey ft. Boyz II Men"));
		mongo.updateOne(collection, before, after);

		Document findQuery = new Document("weeksAtOne", new Document("$gte",10));
		Document orderBy = new Document("decade", 1);
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery, orderBy);

		while(cursor.hasNext()){
			Document doc = cursor.next();
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
