package nosql.mongo;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import interfaces.MFMusic;

public class MongoServiceGetId {
	
	private static MongoService ms = MongoService.getInstance();
	
	@SuppressWarnings("unchecked")
	static List<String> getIdMusicsByTag(String tag){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.TAGS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document(MongoCollectionsAndKeys.TAG_TAGS, new Document("$eq",tag));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		
		List<String> listIdMusic = new ArrayList<String>();
		if(cursor.hasNext()){
			listIdMusic = (ArrayList<String>) cursor.next().get(MongoCollectionsAndKeys.IDMUSIC_TAGS);
		}
		return listIdMusic;
	}

	static List<String> getIdMusicsByIdArtist(String idArtist){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document(MongoCollectionsAndKeys.IDARTIST_MUSICS, new Document("$eq",idArtist));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		List<String> listeId = new ArrayList<String>();
		Document doc;
		while(cursor.hasNext()){
			doc = cursor.next();
			listeId.add(doc.getString(MongoCollectionsAndKeys.IDMUSIC_MUSICS));
		}
		return listeId;
	}

	static List<String> getIdMusicsByChainWords(String chainWords){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document(MongoCollectionsAndKeys.LYRICS_MUSICS, new Document("$regex",chainWords));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		ArrayList<String> listeId = new ArrayList<String>();
		while(cursor.hasNext()){
			listeId.add(cursor.next().getString(MongoCollectionsAndKeys.IDMUSIC_MUSICS));
		}
		return listeId;
	}

	static List<String> getAllAlbumIds(){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.ALBUMS); // récupère la collection mongo qui stocke tous les albums
		MongoCursor<Document> cursor = ms.findAll(collection);

		List<String> allIdAlbum = new ArrayList<String>(); 
		while(cursor.hasNext()){
			allIdAlbum.add(cursor.next().getString(MongoCollectionsAndKeys.IDALBUM_ALBUMS));
		}
		return allIdAlbum;
	}

	static MFMusic getMusicById(String idMusic) {
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document findQuery = new Document(MongoCollectionsAndKeys.IDMUSIC_MUSICS, new Document("$eq",idMusic));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		MFMusic music = null;
		if(cursor.hasNext()){
			music = MongoUtils.transformDocumentIntoMFMusic(cursor.next());
		}
		return music;
	}
}
