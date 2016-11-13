package nosql.mongo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import utils.MuzikFinderPreferences;

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

	static Set<String> getSetNameArtistBeginWith(String nameArtist){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document findQuery = new Document(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS, new Document("$regex","^"+nameArtist).append("$options", "i"));
		MongoCursor<Document> cursor = ms.findByWithLimit(collection, findQuery, MuzikFinderPreferences.SIZE_OF_TYPEAHEAD);

		Set<String> setNameArtist = new HashSet<String>();
		while(cursor.hasNext()){
			setNameArtist.add(cursor.next().getString(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS));
		}
		return setNameArtist;
	}
	
	static Set<String> getSetTrackNameBeginWith(String trackName){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document findQuery = new Document(MongoCollectionsAndKeys.MUSICNAME_MUSICS, new Document("$regex","^"+trackName).append("$options", "i"));
		MongoCursor<Document> cursor = ms.findByWithLimit(collection, findQuery, MuzikFinderPreferences.SIZE_OF_TYPEAHEAD);

		Set<String> setTrackNAme = new HashSet<String>();
		while(cursor.hasNext()){
			setTrackNAme.add(cursor.next().getString(MongoCollectionsAndKeys.MUSICNAME_MUSICS));
		}
		return setTrackNAme;
	}
}
