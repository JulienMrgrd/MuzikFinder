package nosql;


import java.util.HashSet;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import nosql.mongo.MongoService;

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

	public boolean insertTagIfNotExists(String tag, String musicId){
		MongoCollection<Document> collection = mongo.getCollection("Tags");
		Document doc;
		if(!presentTag(tag)){
			doc = new Document();
			doc.put("tag",tag);
			doc.put("musicId",musicId);
			mongo.insertOne(collection, doc);
			return true;
		}
		else{
			if(presentIdMusicOnTag(tag,musicId)){
				System.out.println("IdMusic already corresponding in the Tag Collection\n");
				return false;
			}
			doc = new Document("tag", new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = mongo.findBy(collection, doc);
			while(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				String listeId = doc1.getString("musicId");
				System.out.println(listeId);
				listeId = listeId.concat(";"+musicId);
				doc2 = new Document("$set",new Document("musicId",listeId));
				mongo.updateOne(collection, doc1,doc2);
				return true;
			}
		}
		return true;
	}
	
	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId, String nameMusic, String langue, String spotifyId, String soundCloudId){
		if(presentLyrics(musicId)){
			System.out.println("Lyrics already presents in the collection\n");
			return false; 
		}
		MongoCollection<Document> collection = mongo.getCollection("Lyrics");
		Document doc = new Document();
		doc.put("idMusic",musicId);
		doc.put("lyrics",words);
		doc.put("idArtist",artistId);
		doc.put("nameMusic", nameMusic);
		doc.put("langue", langue);
		doc.put("spotifyId", spotifyId);
		doc.put("soundCloudId",soundCloudId);
		mongo.insertOne(collection, doc);
		return true;
	}

	public boolean insertArtistIfNotExist(String artistName, String artistId ){
		if(presentArtist(artistId)){
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

	public boolean presentArtist(String artistId){
		MongoCollection<Document> collection = mongo.getCollection("Artists"); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document("nameArtist", new Document("$eq",artistId)); // crée le document retournant les informations pr�sentes dans la collection Artists correspondantes
		MongoCursor<Document> cursor = mongo.findBy(collection, doc);
		while(cursor.hasNext())
			return true;
		return false;
	}
	
	public boolean presentLyrics(String musicId){
		MongoCollection<Document> collection = mongo.getCollection("Lyrics"); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document("idMusic", new Document("$eq",musicId)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = mongo.findBy(collection, doc);
		while(cursor.hasNext())
			return true;
		return false;
	}
	
	public boolean presentTag(String tag){
		MongoCollection<Document> collection = mongo.getCollection("Tags");
		Document findQuery = new Document("tag", new Document("$eq",tag));
		System.out.println(findQuery);
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery);
		while(cursor.hasNext())
			return true;
		return false;
	}
	
	public boolean presentIdMusicOnTag(String tag, String idMusic){
		MongoCollection<Document> collection = mongo.getCollection("Tags"); // récupère la collection mongo qui stocke les musiques
		
		Document doc = new Document("tag",new Document("$regex",tag)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = mongo.findBy(collection, doc);
		while(cursor.hasNext()){
			Document doc1 = cursor.next();
			String chaine = doc1.getString("musicId");
			//System.out.println(chaine);
			String[] tab = chaine.split(";");
			for( String s : tab){
				if(s.equals(idMusic))
					return true;
			}
		}
		return false;
	}

	public Set<String> getMusicByTag(String tag){
		
		MongoCollection<Document> collection = mongo.getCollection("Tags"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery);
		
		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			listeId.add(doc.getString("idMusic"));
		}
		return listeId;
	}
	
	public Set<String> getMusicByIdArtist(String idArtiste){
		
		MongoCollection<Document> collection = mongo.getCollection("Lyrics"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("idArtist", new Document("$eq",idArtiste));
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery);
		
		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			listeId.add(doc.getString("idMusic"));
		}
		return listeId;
	}
	
	public String getidArtist(String nameArtiste){
		
		MongoCollection<Document> collection = mongo.getCollection("Artists"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("nameArtist", new Document("$eq",nameArtiste));
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery);
		
		while(cursor.hasNext()){
			Document doc = cursor.next();
			return doc.getString("idArtist");
		}
		return null;
	}
	
	public Set<String> getMusicByLyric(String lyric){
		
		MongoCollection<Document> collection = mongo.getCollection("Lyrics"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("decade", new Document("$regex",lyric));
		System.out.println(findQuery);
		MongoCursor<Document> cursor = mongo.findBy(collection, findQuery);
		
		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			listeId.add(doc.getString("decade"));
		}
		return listeId;
	}
}
