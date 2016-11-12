package nosql.mongo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class MongoServiceGetId {
	
	private static MongoService ms = MongoService.getInstance();
	
	static List<String> getAllAlbumIds(){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.ALBUMS); // récupère la collection mongo qui stocke tous les albums
		MongoCursor<Document> cursor = ms.findAll(collection);

		List<String> allIdAlbum = new ArrayList<String>(); 
		while(cursor.hasNext()){
			allIdAlbum.add(cursor.next().getString(MongoCollectionsAndKeys.ALBUMID_ALBUMS));
		}
		return allIdAlbum;
	}
	
	@SuppressWarnings("unchecked")
	static List<Document> getListDocumentIdMusicScoreByTag(String tag){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.TAGS);
		Document findQuery = new Document(MongoCollectionsAndKeys.TAG_TAGS, new Document("$eq",tag.toLowerCase()));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);
		
		List<Document> listIdMusicDocument = new ArrayList<Document>();
		if(cursor.hasNext()){
			return (ArrayList<Document>) cursor.next().get(MongoCollectionsAndKeys.IDMUSICS_TAGS);
		}
		return listIdMusicDocument;
	}

	static List<String> getListNameArtistBeginWith(String nameArtist){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document findQuery = new Document(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS, new Document("$regex","^"+nameArtist).append("$options", "i"));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		Set<String> setNameArtist = new HashSet<String>();
		while(cursor.hasNext() && setNameArtist.size()<6){
			setNameArtist.add(cursor.next().getString(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS));
		}
		List<String> listNameArtist = new ArrayList<String>(setNameArtist);
		return listNameArtist;
	}
	
	static List<String> getListTrackNameBeginWith(String trackName){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document findQuery = new Document(MongoCollectionsAndKeys.MUSICNAME_MUSICS, new Document("$regex","^"+trackName).append("$options", "i"));
		MongoCursor<Document> cursor = ms.findBy(collection, findQuery);

		Set<String> setTrackNAme = new HashSet<String>();
		while(cursor.hasNext() && setTrackNAme.size()<6){
			setTrackNAme.add(cursor.next().getString(MongoCollectionsAndKeys.MUSICNAME_MUSICS));
		}
		List<String> listTrackNAme = new ArrayList<String>(setTrackNAme);
		return listTrackNAme;
	}
}
