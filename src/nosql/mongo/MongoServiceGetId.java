package nosql.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoServiceGetId {
	
	@SuppressWarnings("unchecked")
	public static List<String> getIdMusicsByTag(String tag, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.TAGS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		
		List<String> listIdMusic = null;
		if(cursor.hasNext()){
			listIdMusic = (ArrayList<String>) cursor.next().get("idMusic");
		}
		return listIdMusic;
	}

	public static List<String> getIdMusicsByIdArtist(String idArtist, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("idArtist", new Document("$eq",idArtist));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		List<String> listeId = new ArrayList<String>();
		Document doc;
		while(cursor.hasNext()){
			doc = cursor.next();
			listeId.add(doc.getString("idMusic"));
		}
		return listeId;
	}

	public static String getIdArtist(String nameArtiste, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.ARTISTS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("nameArtist", new Document("$eq",nameArtiste));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		if(cursor.hasNext()){
			return cursor.next().getString("idArtist");
		}
		return null;
	}

	public static List<String> getIdMusicsByChainWords(String chainWords, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("lyrics", new Document("$regex",chainWords));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		ArrayList<String> listeId = new ArrayList<String>();
		while(cursor.hasNext()){
			listeId.add(cursor.next().getString("idMusic"));
		}
		return listeId;
	}

	public static List<String> getAllAlbumIds(MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.ALBUMS); // récupère la collection mongo qui stocke tous les albums
		MongoCursor<Document> cursor = ms.findAll(collection);

		List<String> allIdAlbum = new ArrayList<String>(); 
		while(cursor.hasNext()){
			allIdAlbum.add(cursor.next().getString("idAlbum"));
		}
		return allIdAlbum;
	}
}
