package nosql.mongo;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoServiceContains {
	
	private static MongoService ms = MongoService.getInstance();
	
	static boolean containsMusic(String musicId){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document(MongoCollectionsAndKeys.IDMUSIC_MUSICS, new Document("$eq",musicId)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		return cursor.hasNext();
	}

	static boolean containsTag(String tag){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.TAGS);
		Document findQuery = new Document(MongoCollectionsAndKeys.TAG_TAGS, new Document("$eq",tag));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		return cursor.hasNext();
	}

	@SuppressWarnings("unchecked")
	static boolean containsIdMusicInTag(String tag, String idMusic){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.TAGS); // récupère la collection mongo qui stocke les tags

		Document doc = new Document(MongoCollectionsAndKeys.TAG_TAGS,new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		
		Document doc_new;
		List<Document> listDocument;
		while(cursor.hasNext()){
			doc_new = cursor.next();
			listDocument = (List<Document>) doc_new.get(MongoCollectionsAndKeys.MUSICID_TAGS);
			for(Document doc2 : listDocument){
				if(doc2.getString("idMusic").equals(idMusic)) return true;
			}
		}
		return false;
	}

	static boolean containsIdAlbum(String idAlbum){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.ALBUMS);
		Document doc = new Document(MongoCollectionsAndKeys.ALBUMID_ALBUMS,new Document("$eq",idAlbum));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		return cursor.hasNext();
	}

	static boolean containsIdRecherche(String idRecherche){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.CACHE);
		Document findQuery = new Document(MongoCollectionsAndKeys.SEARCHID_CACHE, new Document("$eq",idRecherche));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		return cursor.hasNext();
	}
}
