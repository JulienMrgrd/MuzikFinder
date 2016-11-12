package nosql.mongo;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class MongoServicePreference {
	
	private static MongoService ms = MongoService.getInstance();
	
	/**
	 * Méthode permettant de modifier dans mongo la préference de nom "prefName" en lui attribuant la valeur "param"
	 * @param prefName nom de la préference à modifier
	 * @param param nouvelle valeur à attribuer à la préference
	 */
	static void setPref(String prefName, String param) {
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.PREFS);
		Document oldPref = ms.findFirst(collection);
		if(oldPref != null) {
			Document newPref = new Document("$set",new Document(prefName, param));
			ms.updateOne(collection, oldPref, newPref);
		} else {
			Document newPref = new Document(prefName, param);
			ms.insertOne(collection, newPref);
		}
	}

	/**
	 * Méthode permettant de récupérer dans mongo la préference de nom "prefName"
	 * @param prefName nom de la préference à récupérer
	 */
	static String getPref(String prefName) {
		try {
			MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.PREFS);
			Document doc = ms.findFirst(collection);
			return doc.getString(prefName);
		} catch (Exception e){
			return null;
		}
	}
	
}
