package nosql.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import server.services.Search;

public class MongoService {

	ServerAddress serverAddress;
	MongoCredential mongoCredential;
	MongoClient mongoClient;
	MongoDatabase db;

	public MongoService() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.WARNING); // e.g. or Log.WARNING, etc.
		
		// Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
		serverAddress = new ServerAddress("ds049456.mlab.com", 49456);
		mongoCredential = MongoCredential.createCredential("heroku_1dpqh3kq", "heroku_1dpqh3kq",
				"gv7mru79jbtgn52lrl5mg301qh".toCharArray());
		mongoClient = new MongoClient(serverAddress, Arrays.asList(mongoCredential));
		db = mongoClient.getDatabase("heroku_1dpqh3kq");
	}


	// this creates collection if not exists
	public void insertMany(MongoCollection<Document> collection, List<Document> docs){
		collection.insertMany(docs);
	}

	// this creates collection if not exists
	public void insertOne(MongoCollection<Document> collection, Document doc){
		collection.insertOne(doc);
	}

	// this creates collection if not exists
	public void updateOne(MongoCollection<Document> collection, Document before, Document after){
		collection.updateOne(before, after);
	}

	public MongoCursor<Document> findAll(MongoCollection<Document> collection){
		return collection.find().iterator();
	}

	public MongoCursor<Document> findBy(MongoCollection<Document> collection, Document findQuery){
		return collection.find(findQuery).iterator();
	}

	public MongoCursor<Document> findBy(MongoCollection<Document> collection, 
			Document findQuery, Document orderBy){
		return collection.find(findQuery).sort(orderBy).iterator();
	}

	public MongoCollection<Document> getCollection(String collectionName){
		return db.getCollection(collectionName);
	}

	public boolean dropCollection(String collectionName){
		try{
			db.getCollection(collectionName).drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	public boolean dropCollection(MongoCollection<Document> collection){
		try{
			collection.drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	public void close(){
		mongoClient.close();
	}
	
	// Example
	public List<Document> createFakeDocuments(){

		Document seventies = new Document();
		seventies.put("decade", "1970s");
		seventies.put("artist", "Debby Boone");
		seventies.put("song", "You Light Up My Life");
		seventies.put("weeksAtOne", 10);

		Document eighties = new Document();
		eighties.put("decade", "1980s");
		eighties.put("artist", "Olivia Newton-John");
		eighties.put("song", "Physical");
		eighties.put("weeksAtOne", 10);

		Document nineties = new Document();
		nineties.put("decade", "1990s");
		nineties.put("artist", "Mariah Carey");
		nineties.put("song", "One Sweet Day");
		nineties.put("weeksAtOne", 16);
		List<Document> fakeData = new ArrayList<>(3);
		fakeData.add(seventies);
		fakeData.add(eighties);
		fakeData.add(nineties);
		return fakeData;
	}
	
	public void fakeUse(){
		System.out.println("on entre ici");
		MongoCollection<Document> collection = this.getCollection("songs");
		
		List<Document> docs = this.createFakeDocuments();
		this.insertMany(collection, docs);

		Document before = new Document("song", "One Sweet Day");
		Document after = new Document("$set", new Document("artist", "Mariah Carey ft. Boyz II Men"));
		this.updateOne(collection, before, after);

		Document findQuery = new Document("weeksAtOne", new Document("$gte",10));
		Document orderBy = new Document("decade", 1);
		MongoCursor<Document> cursor = this.findBy(collection, findQuery, orderBy);
		System.out.println(findQuery.isEmpty());
		while(cursor.hasNext()){
			System.out.println("Je rentre ici");
			Document doc = cursor.next();
			Document doc2;
			String listeId = doc.getString("decade");
			System.out.println(listeId);
			listeId=listeId.concat(";dklknvlk");
			System.out.println(listeId);
			doc2=new Document("$set",new Document("decade", listeId));
			this.updateOne(collection, doc, doc2);
			System.out.println(
					"In the " + doc.get("decade") + ", " + doc.get("song") + 
					" by " + doc.get("artist") + " topped the charts for " + 
					doc.get("weeksAtOne") + " straight weeks."
					);
		}

		//this.dropCollection(collection);

		this.close();
	}

	public static void main(String[] args){
		//new MongoService().fakeUse();
		List<String> ls=new ArrayList<>();
		ls.add("Sorry");
		ls.add("wanted");
		Search.searchbyTag(ls);
	}

}
