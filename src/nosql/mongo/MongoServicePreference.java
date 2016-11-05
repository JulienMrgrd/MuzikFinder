package nosql.mongo;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class MongoServicePreference {
	
	public static void setPref(String prefName, String param, MongoService ms) {
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.PREFS);
		Document oldPref = ms.findFirst(collection);
		if(oldPref != null) {
			Document newPref = new Document("$set",new Document(prefName, param));
			ms.updateOne(collection, oldPref, newPref);
		} else {
			Document newPref = new Document(prefName, param);
			ms.insertOne(collection, newPref);
		}
	}

	public static String getPref(String prefName, MongoService ms) {
		try {
			MongoCollection<Document> collection = ms.getCollection(MongoCollections.PREFS);
			Document doc = ms.findFirst(collection);
			return doc.getString(prefName);
		} catch (Exception e){
			return null;
		}
	}
	
}
