package nosql.mongo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.*;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import interfaces.MFLyrics;
import interfaces.MFMusic;
import opennlp.tools.util.InvalidFormatException;
import server.services.Search;
import utils.textMining.ParserTest;

public class MongoService {

	ServerAddress serverAddress;
	MongoCredential mongoCredential;
	MongoClient mongoClient;
	MongoDatabase db;

	public MongoService() {
		Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.WARNING); // e.g. or Log.WARNING, etc.

		// Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
		serverAddress = new ServerAddress("ds049456.mlab.com", 49456);
		mongoCredential = MongoCredential.createCredential("heroku_1dpqh3kq", "heroku_1dpqh3kq",
				"gv7mru79jbtgn52lrl5mg301qh".toCharArray());
		mongoClient = new MongoClient(serverAddress, Arrays.asList(mongoCredential));
		db = mongoClient.getDatabase("heroku_1dpqh3kq");
	}


	// this creates collection if not exists
	public void insertMany(MongoCollection<Document> collection, List<Document> docs){
		collection.insertMany(docs);
	}

	// this creates collection if not exists
	public void insertOne(MongoCollection<Document> collection, Document doc){
		collection.insertOne(doc);
	}

	// this creates collection if not exists
	public void updateOne(MongoCollection<Document> collection, Document before, Document after){
		collection.updateOne(before, after);
	}

	public MongoCursor<Document> findAll(MongoCollection<Document> collection){
		return collection.find().iterator();
	}

	public MongoCursor<Document> findBy(MongoCollection<Document> collection, Document findQuery){
		return collection.find(findQuery).iterator();
	}

	public MongoCursor<Document> findBy(MongoCollection<Document> collection, 
			Document findQuery, Document orderBy){
		return collection.find(findQuery).sort(orderBy).iterator();
	}

	public MongoCollection<Document> getCollection(String collectionName){
		return db.getCollection(collectionName);
	}

	public boolean dropCollection(String collectionName){
		try{
			db.getCollection(collectionName).drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	public boolean dropCollection(MongoCollection<Document> collection){
		try{
			collection.drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	public boolean insertTagIfNotExists(String tag, String musicId){
		MongoCollection<Document> collection = getCollection("Tags");
		Document doc;
		if(!presentTag(tag)){
			doc = new Document();
			doc.put("tag",tag);
			doc.put("idMusic",musicId);
			insertOne(collection, doc);
			return true;
		}else if(presentIdMusicOnTag(tag,musicId)){
			System.out.println("IdMusic already corresponding in the Tag Collection\n");
			return false;
		} else {
			doc = new Document("tag", new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = findBy(collection, doc);
			while(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				String listeId = doc1.getString("idMusic");
				System.out.println(listeId);
				listeId = listeId.concat(";"+musicId);
				doc2 = new Document("$set",new Document("idMusic",listeId));
				updateOne(collection, doc1,doc2);
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
		MongoCollection<Document> collection = getCollection("Musics");
		Document doc = new Document();
		doc.put("idMusic",musicId);
		doc.put("lyrics",words);
		doc.put("idArtist",artistId);
		doc.put("nameMusic", nameMusic);
		doc.put("langue", langue);
		doc.put("spotifyId", spotifyId);
		doc.put("soundCloudId",soundCloudId);
		insertOne(collection, doc);
		return true;
	}

	public boolean insertArtistIfNotExist(String artistName, String artistId ){
		if(presentArtist(artistId)){
			System.out.println("Artist already present in the collection\n");
			return false;
		}
		MongoCollection<Document> collection = getCollection("Artists"); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document();
		doc.put("idArtist", artistId);
		doc.put("nameArtist",artistName);
		insertOne(collection, doc);
		return true;
	}

	public boolean insertIdAlbumIfNotExist(String idAlbum){
		if(presentIdAlbum(idAlbum)){
			System.out.println("Artist already present in the collection\n");
			return false;
		}

		MongoCollection<Document> collection = getCollection("Albums");
		Document doc = new Document();
		doc.put("idAlbum", idAlbum);
		insertOne(collection,doc);
		return true;
	}

	public boolean presentArtist(String artistId){
		MongoCollection<Document> collection = getCollection("Artists"); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document("nameArtist", new Document("$eq",artistId)); // crée le document retournant les informations pr�sentes dans la collection Artists correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		while(cursor.hasNext())
			return true;
		return false;
	}

	public boolean presentLyrics(String musicId){
		MongoCollection<Document> collection = getCollection("Musics"); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document("idMusic", new Document("$eq",musicId)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		while(cursor.hasNext())
			return true;
		return false;
	}

	public boolean presentTag(String tag){
		MongoCollection<Document> collection = getCollection("Tags");
		Document findQuery = new Document("tag", new Document("$eq",tag));
		System.out.println(findQuery);
		MongoCursor<Document> cursor = findBy(collection, findQuery);
		while(cursor.hasNext())
			return true;
		return false;
	}

	public boolean presentIdMusicOnTag(String tag, String idMusic){
		MongoCollection<Document> collection = getCollection("Tags"); // récupère la collection mongo qui stocke les musiques

		Document doc = new Document("tag",new Document("$regex",tag)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		while(cursor.hasNext()){
			Document doc1 = cursor.next();
			String chaine = doc1.getString("idMusic");
			//System.out.println(chaine);
			String[] tab = chaine.split(";");
			for( String s : tab){
				if(s.equals(idMusic))
					return true;
			}
		}
		return false;
	}

	public boolean presentIdAlbum(String idAlbum){
		MongoCollection<Document> collection = getCollection("Albums");
		Document doc = new Document("idAlbum",new Document("$eq",idAlbum));
		MongoCursor<Document> cursor = findBy(collection, doc);

		while(cursor.hasNext())
			return true;
		return false;
	}


	public String getMusicsByTag(String tag){
		MongoCollection<Document> collection = getCollection("Tags"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = findBy(collection, findQuery);
		String idMusic="";
		while(cursor.hasNext()){
			Document doc = cursor.next();
			idMusic+=doc.getString("idMusic");
		}
		return idMusic;
	}

	public Set<String> getMusicsByIdArtist(String idArtiste){
		MongoCollection<Document> collection = getCollection("Musics"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("idArtist", new Document("$eq",idArtiste));
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			listeId.add(doc.getString("idMusic"));
		}
		return listeId;
	}


	public String getIdArtist(String nameArtiste){
		MongoCollection<Document> collection = getCollection("Artists"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("nameArtist", new Document("$eq",nameArtiste));
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		while(cursor.hasNext()){
			Document doc = cursor.next();
			return doc.getString("idArtist");
		}
		return null;
	}


	public Set<String> getMusicsByLyrics(String lyrics){
		MongoCollection<Document> collection = getCollection("Musics"); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("lyrics", new Document("$regex",lyrics));
		System.out.println(findQuery);
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			listeId.add(doc.getString("idMusic"));
		}
		return listeId;
	}


	public List<String> getAllIdAlbum(){
		MongoCollection<Document> collection = getCollection("Albums"); // récupère la collection mongo qui stocke tous les albums
		MongoCursor<Document> cursor = findAll(collection);

		List<String> allIdAlbum = new ArrayList<String>(); 
		while(cursor.hasNext()){
			Document doc = cursor.next();
			allIdAlbum.add(doc.getString("idAlbum"));
		}
		return allIdAlbum;
	}


	public List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
		List<String> idAlbumAlreadyExist = getAllIdAlbum();

		if(idAlbumAlreadyExist==null){
			return musics;
		}
		List<MFMusic> listReduce = new ArrayList<MFMusic>();
		for(MFMusic mfm : musics){
			if(!idAlbumAlreadyExist.contains(mfm.getAlbumId())){
				listReduce.add(mfm);
			}
		}
		return listReduce;
	}


	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum) throws Exception{
		ArrayList<String> listIdAlbum = (ArrayList<String>) mapAlbumIdWithAlbum.keySet();

		ArrayList<String> nounPhrases = new ArrayList<String>(); // noms dans la lyric pour les tags
		ArrayList<String> adjectivesPhrases = new ArrayList<String>(); // adjectifs dans la lyric pour les tags
		ArrayList<String> verbsPhrases = new ArrayList<String>(); // verbes dans la lyric pour les tags

		InputStream is = new FileInputStream("res/en-parser-chunking.bin");
		ParserModel model = new ParserModel(is);
		Parser parser = ParserFactory.create(model);
		// Utiliser pour la création des tags de chaque lyrics
		
		for(String idAlbum : listIdAlbum){
			insertIdAlbumIfNotExist(idAlbum); // On insère l'id dans l'album dans la collection Albums
			ArrayList<MFMusic> listMusic = new ArrayList<MFMusic>(mapAlbumIdWithAlbum.get(idAlbum));
			
			for(MFMusic mf : listMusic){
				// Pour chaque MFMusic présentes dans l'album
				MFLyrics mfL = mf.getLyrics();
				// on récupère les lyrics
				// et on insère dans la base mongo Lyrics
				insertLyricsIfNotExists(mfL.getLyricsBody(), mf.getTrackId(),
						mf.getArtistId(), mf.getTrackName(),mfL.getLyrics_language(), mf.getTrackSpotifyId()
						, mf.getTrackSoundcloudId());
				
				// Début de la création des tags pour chaque lyrics

				Parse topParses[] = ParserTool.parseLine(mfL.getLyricsBody(), parser, 1);
				for (Parse p : topParses){
					ParserTest.getNounPhrases(p);
				}
				// On stocke tout les tags par type dans les variables suivantes
				nounPhrases = (ArrayList<String>) ParserTest.getNounPhrases();
				adjectivesPhrases = (ArrayList<String>) ParserTest.getAdjectivePhrases();
				verbsPhrases = (ArrayList<String>) ParserTest.getVerbPhrases();
				// et on les ajoute dans la base mongo Tags sans oublier de retirer les caractères
				// spéciaux
				for(String s : nounPhrases){
					s = s.replaceAll("[^A-Za-z0-9]", "");
					insertTagIfNotExists(s, mf.getTrackId());
				}
				for(String s : adjectivesPhrases){
					s = s.replaceAll("[^A-Za-z0-9]", "");
					insertTagIfNotExists(s, mf.getTrackId());
				}
				for(String s : verbsPhrases){
					s = s.replaceAll("[^A-Za-z0-9]", "");
					insertTagIfNotExists(s, mf.getTrackId());
				}
			}
		}
	}

	public void close(){
		mongoClient.close();
	}

	// Example
	public List<Document> createFakeDocuments(){

		Document seventies = new Document();
		seventies.put("decade", "1970s");
		seventies.put("artist", "Debby Boone");
		seventies.put("song", "You Light Up My Life");
		seventies.put("weeksAtOne", 10);

		Document eighties = new Document();
		eighties.put("decade", "1980s");
		eighties.put("artist", "Olivia Newton-John");
		eighties.put("song", "Physical");
		eighties.put("weeksAtOne", 10);

		Document nineties = new Document();
		nineties.put("decade", "1990s");
		nineties.put("artist", "Mariah Carey");
		nineties.put("song", "One Sweet Day");
		nineties.put("weeksAtOne", 16);
		List<Document> fakeData = new ArrayList<>(3);
		fakeData.add(seventies);
		fakeData.add(eighties);
		fakeData.add(nineties);
		return fakeData;
	}

	public void fakeUse(){
		System.out.println("on entre ici");
		MongoCollection<Document> collection = this.getCollection("songs");

		List<Document> docs = this.createFakeDocuments();
		this.insertMany(collection, docs);

		Document before = new Document("song", "One Sweet Day");
		Document after = new Document("$set", new Document("artist", "Mariah Carey ft. Boyz II Men"));
		this.updateOne(collection, before, after);

		Document findQuery = new Document("weeksAtOne", new Document("$gte",10));
		Document orderBy = new Document("decade", 1);
		MongoCursor<Document> cursor = this.findBy(collection, findQuery, orderBy);
		System.out.println(findQuery.isEmpty());
		while(cursor.hasNext()){
			System.out.println("Je rentre ici");
			Document doc = cursor.next();
			Document doc2;
			String listeId = doc.getString("decade");
			System.out.println(listeId);
			listeId=listeId.concat(";dklknvlk");
			System.out.println(listeId);
			doc2=new Document("$set",new Document("decade", listeId));
			this.updateOne(collection, doc, doc2);
			System.out.println(
					"In the " + doc.get("decade") + ", " + doc.get("song") + 
					" by " + doc.get("artist") + " topped the charts for " + 
					doc.get("weeksAtOne") + " straight weeks."
					);
		}

		//this.dropCollection(collection);

		this.close();
	}

	public static void main(String[] args){
		//new MongoService().fakeUse();
		List<String> ls=new ArrayList<>();
		ls.add("Sorry");
		ls.add("wanted");
		Search.searchbyTag(ls);
	}

}
