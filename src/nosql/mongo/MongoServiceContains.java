package nosql.mongo;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoServiceContains {
	public static boolean containsArtist(String artistId, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.ARTISTS); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document("nameArtist", new Document("$eq",artistId)); // crée le document retournant les informations pr�sentes dans la collection Artists correspondantes
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		return cursor.hasNext();
	}

	public static boolean containsLyrics(String musicId, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document("idMusic", new Document("$eq",musicId)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		return cursor.hasNext();
	}

	public static boolean containsTag(String tag, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.TAGS);
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		return cursor.hasNext();
	}

	@SuppressWarnings("unchecked")
	public static boolean containsIdMusicInTag(String tag, String idMusic, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.TAGS); // récupère la collection mongo qui stocke les tags

		Document doc = new Document("tag",new Document("$regex",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = ms.findBy(collection, doc);
		
		Document doc_new;
		List<String> listIdMusic;
		
		while(cursor.hasNext()){
			doc_new = cursor.next();
			listIdMusic = (List<String>) doc_new.get("idMusic");
			for( String s : listIdMusic ){
				if(s.equals(idMusic)) return true;
			}
		}
		return false;
	}

	public static boolean containsIdAlbum(String idAlbum, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.ALBUMS);
		Document doc = new Document("idAlbum",new Document("$eq",idAlbum));
		MongoCursor<Document> cursor = ms.findBy(collection, doc);

		return cursor.hasNext();
	}

}
