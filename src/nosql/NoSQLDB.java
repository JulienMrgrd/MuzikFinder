package nosql;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import nosql.mongo.MongoService;
import opennlp.tools.util.featuregen.DictionaryFeatureGenerator;

/**
 * Appel les fonctions de la BD choisies (MongoDB, DynamoDB, etc ...)
 * @author JulienM
 */
public class NoSQLDB {

	private MongoService mongo;

	public NoSQLDB() {
		mongo = new MongoService();
	}

	// TODO : to delete
	public void fakeUse(){
		mongo.fakeUse();
	}

	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId ){
		if(presentLyrics(words)){
			System.out.println("Lyrics already presents in the collection\n");
			return false; 
		}
		MongoCollection<Document> collection = mongo.getCollection("Lyrics");
		Document doc = new Document();
		doc.put("idMusic",musicId);
		doc.put("lyrics",words);
		doc.put("idArtist",artistId);
		mongo.insertOne(collection, doc);
		return true;
	}

	public boolean insertArtistIfNotExist(String artistName, String artistId ){
		if(presentArtist(artistName)){
			System.out.println("Artist already present in the collection\n");
			return false;
		}
		MongoCollection<Document> collection = mongo.getCollection("Artists"); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document();
		doc.put("idArtist", artistId);
		doc.put("nameArtist",artistName);
		mongo.insertOne(collection, doc);
		return true;
	}

	//TODO : permet de chercher un artiste dans la base mongoDB. Retourne vrai si il est présent, non sinon.

	public boolean presentArtist(String artistName){
		MongoCollection<Document> collection = mongo.getCollection("Artists"); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document("artisteName", new Document("$eq",artistName)); // crée le document retournant les informations présentes dans la collection Artists correspondantes
		MongoCursor<Document> cursor = mongo.findBy(collection, doc);
		while(cursor.hasNext())
			return true;
		return false;
	}
	
	public boolean presentLyrics(String lyrics){
		MongoCollection<Document> collection = mongo.getCollection("Lyrics"); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document("lyrics", new Document("$eq",lyrics)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = mongo.findBy(collection, doc);
		while(cursor.hasNext())
			return true;
		return false;
	}
}
