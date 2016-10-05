package nosql.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoService {

	ServerAddress serverAddress;
	MongoCredential mongoCredential;
	MongoClient mongoClient;
	DB db;

	public MongoService() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.WARNING); // e.g. or Log.WARNING, etc.
		
		// Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
		serverAddress = new ServerAddress("ds049456.mlab.com", 49456);
		mongoCredential = MongoCredential.createCredential("heroku_1dpqh3kq", "heroku_1dpqh3kq", "gv7mru79jbtgn52lrl5mg301qh".toCharArray());
		mongoClient = new MongoClient(serverAddress, Arrays.asList(mongoCredential));
		db = mongoClient.getDB("heroku_1dpqh3kq");
	}


	// this creates collection if not exists
	public void insertMany(DBCollection collection, List<BasicDBObject> docs){
		collection.insert(docs);
	}

	// this creates collection if not exists
	public void insertOne(DBCollection collection, BasicDBObject doc){
		collection.insert(doc);
	}

	// this creates collection if not exists
	public void updateOne(DBCollection collection, BasicDBObject before, BasicDBObject after){
		collection.update(before, after);
	}

	public DBCursor findAll(DBCollection collection, BasicDBObject findQuery){
		return collection.find();
	}

	public DBCursor findBy(DBCollection collection, BasicDBObject findQuery){
		return collection.find(findQuery);
	}

	public DBCursor findBy(DBCollection collection, BasicDBObject findQuery, BasicDBObject orderBy){
		return collection.find(findQuery).sort(orderBy);
	}

	public DBCollection getCollection(String collectionName){
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

	public boolean dropCollection(DBCollection collection){
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
	public List<BasicDBObject> createFakeBasicDBObjects(){

		BasicDBObject seventies = new BasicDBObject();
		seventies.put("decade", "1970s");
		seventies.put("artist", "Debby Boone");
		seventies.put("song", "You Light Up My Life");
		seventies.put("weeksAtOne", 10);

		BasicDBObject eighties = new BasicDBObject();
		eighties.put("decade", "1980s");
		eighties.put("artist", "Olivia Newton-John");
		eighties.put("song", "Physical");
		eighties.put("weeksAtOne", 10);

		BasicDBObject nineties = new BasicDBObject();
		nineties.put("decade", "1990s");
		nineties.put("artist", "Mariah Carey");
		nineties.put("song", "One Sweet Day");
		nineties.put("weeksAtOne", 16);
		List<BasicDBObject> fakeData = new ArrayList<>(3);
		fakeData.add(seventies);
		fakeData.add(eighties);
		fakeData.add(nineties);
		return fakeData;
	}
	
	public void fakeUse(){
		DBCollection collection = this.getCollection("songs");
		
		List<BasicDBObject> docs = this.createFakeBasicDBObjects();
		this.insertMany(collection, docs);

		BasicDBObject before = new BasicDBObject("song", "One Sweet Day");
		BasicDBObject after = new BasicDBObject("$set", new BasicDBObject("artist", "Mariah Carey ft. Boyz II Men"));
		this.updateOne(collection, before, after);

		BasicDBObject findQuery = new BasicDBObject("weeksAtOne", new BasicDBObject("$gte",10));
		BasicDBObject orderBy = new BasicDBObject("decade", 1);
		DBCursor cursor = this.findBy(collection, findQuery, orderBy);

		while(cursor.hasNext()){
			DBObject doc = cursor.next();
			System.out.println(
					"In the " + doc.get("decade") + ", " + doc.get("song") + 
					" by " + doc.get("artist") + " topped the charts for " + 
					doc.get("weeksAtOne") + " straight weeks."
					);
		}

		this.dropCollection(collection);

		this.close();
	}

	public static void main(String[] args){
		new MongoService().fakeUse();
	}

}
