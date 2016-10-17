package nosql.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import interfaces.MFLyrics;
import interfaces.MFMusic;
import server.dto.MusicDTO;
import utils.MathUtils;
import utils.textMining.ParserUtils;

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
	private void insertOne(MongoCollection<Document> collection, Document doc){
		collection.insertOne(doc);
	}

	// this creates collection if not exists
	private void updateOne(MongoCollection<Document> collection, Document before, Document after){
		collection.updateOne(before, after);
	}

	private MongoCursor<Document> findAll(MongoCollection<Document> collection){
		return collection.find().iterator();
	}

	private MongoCursor<Document> findBy(MongoCollection<Document> collection, Document findQuery){
		return collection.find(findQuery).iterator();
	}

	@SuppressWarnings("unused")
	private MongoCursor<Document> findBy(MongoCollection<Document> collection, 
			Document findQuery, Document orderBy){
		return collection.find(findQuery).sort(orderBy).iterator();
	}

	public MongoCollection<Document> getCollection(String collectionName){
		return db.getCollection(collectionName);
	}

	@SuppressWarnings("unused")
	private boolean dropCollection(String collectionName){
		try{
			db.getCollection(collectionName).drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean dropCollection(MongoCollection<Document> collection){
		try{
			collection.drop();
			return true;
		} catch (Exception e){
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean insertTagIfNotExists(String tag, String musicId){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS);
		Document doc;
		if(!containsTag(tag)){
			doc = new Document();
			doc.put("tag",tag);
			List<String> listId = new ArrayList<String>(1);
			listId.add(musicId);
			doc.put("idMusic", listId);
			insertOne(collection, doc);
			return true;
		} else if(containsIdMusicOnTag(tag,musicId)){
			System.out.println("IdMusic already corresponding in the Tag Collection\n");
			return false;
		} else {
			doc = new Document("tag", new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = findBy(collection, doc);
			if(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				List<String> listeId = (List<String>) doc1.get("idMusic");
				System.out.println(listeId);
				listeId.add(musicId);
				doc2 = new Document("$set",new Document("idMusic",listeId));
				updateOne(collection, doc1,doc2);
			}
			return true;
		}
	}


	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId, String nameMusic, String langue, String spotifyId, String soundCloudId){
		if(containsLyrics(musicId)) return false; 

		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS);
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

	public boolean insertArtistIfNotExist(String artistName, String artistId){
		if(containsArtist(artistId)) return false;

		MongoCollection<Document> collection = getCollection(MongoCollections.ARTISTS); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document();
		doc.put("idArtist", artistId);
		doc.put("nameArtist",artistName);
		insertOne(collection, doc);
		return true;
	}

	public boolean insertIdAlbumIfNotExist(String idAlbum){
		if(containsIdAlbum(idAlbum)) return false;

		MongoCollection<Document> collection = getCollection(MongoCollections.ALBUMS);
		Document doc = new Document();
		doc.put("idAlbum", idAlbum);
		insertOne(collection,doc);
		return true;
	}

	public boolean containsArtist(String artistId){
		MongoCollection<Document> collection = getCollection(MongoCollections.ARTISTS); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document("nameArtist", new Document("$eq",artistId)); // crée le document retournant les informations pr�sentes dans la collection Artists correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		if(cursor.hasNext()) return true;
		else return false;
	}

	public boolean containsLyrics(String musicId){
		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document("idMusic", new Document("$eq",musicId)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		if(cursor.hasNext()) return true;
		return false;
	}

	public boolean containsTag(String tag){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS);
		Document findQuery = new Document("tag", new Document("$eq",tag));
		System.out.println(findQuery);
		MongoCursor<Document> cursor = findBy(collection, findQuery);
		if(cursor.hasNext()) return true;
		return false;
	}

	//TODO : A MODIFIER
	// FAIT : Moussa Nedjari 17/10/2016 16:45
	public boolean containsIdMusicOnTag(String tag, String idMusic){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS); // récupère la collection mongo qui stocke les musiques

		Document doc = new Document("tag",new Document("$regex",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		while(cursor.hasNext()){
			Document doc_new = cursor.next();
			List<String> listIdMusic = (List<String>)doc_new.get("idMusic");
			for( String s : listIdMusic){
				if(s.equals(idMusic))
					return true;
			}
		}
		return false;
	}

	public boolean containsIdAlbum(String idAlbum){
		MongoCollection<Document> collection = getCollection(MongoCollections.ALBUMS);
		Document doc = new Document("idAlbum",new Document("$eq",idAlbum));
		MongoCursor<Document> cursor = findBy(collection, doc);

		if(cursor.hasNext()) return true;
		else return false;
	}

	//TODO : A MODIFIER
	// FAIT : Moussa Nedjari 17/10/2016 16:41
	public Set<String> getIdMusicsByTag(String tag){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = findBy(collection, findQuery);
		Set<String> listIdMusic= new HashSet<String>();
		if(cursor.hasNext()){
			Document doc = cursor.next();
			listIdMusic = (Set<String>) doc.get("idMusic");
		}
		return listIdMusic;
	}

	public Set<String> getIdMusicsByIdArtist(String idArtist){
		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("idArtist", new Document("$eq",idArtist));
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			listeId.add(doc.getString("idMusic"));
		}
		return listeId;
	}


	public String getIdArtist(String nameArtiste){
		MongoCollection<Document> collection = getCollection(MongoCollections.ARTISTS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("nameArtist", new Document("$eq",nameArtiste));
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		if(cursor.hasNext()){
			return cursor.next().getString("idArtist");
		}
		return null;
	}


	public Set<String> getIdMusicsByChainWords(String chainWords){
		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("lyrics", new Document("$regex",chainWords));
		System.out.println(findQuery);
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		Set<String> listeId = new HashSet<String>();
		while(cursor.hasNext()){
			listeId.add(cursor.next().getString("idMusic"));
		}
		return listeId;
	}


	public List<String> getAllAlbumIds(){
		MongoCollection<Document> collection = getCollection(MongoCollections.ALBUMS); // récupère la collection mongo qui stocke tous les albums
		MongoCursor<Document> cursor = findAll(collection);

		List<String> allIdAlbum = new ArrayList<String>(); 
		while(cursor.hasNext()){
			allIdAlbum.add(cursor.next().getString("idAlbum"));
		}
		return allIdAlbum;
	}


	public List<MFMusic> filterByExistingMusics(List<MFMusic> musics) {
		List<String> idAlbumAlreadyExist = getAllAlbumIds();

		if(idAlbumAlreadyExist==null) return musics;

		List<MFMusic> listReduce = new ArrayList<MFMusic>();
		for(MFMusic mfm : musics){
			if(!idAlbumAlreadyExist.contains(mfm.getAlbumId())){
				listReduce.add(mfm);
			}
		}
		return listReduce;
	}


	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum) throws Exception{
		Set<String> listIdAlbum = mapAlbumIdWithAlbum.keySet();
		String idArtist = "";
		String nameArtist = "";
		//TODO: Insérer Artistes !
		// FAIT : Moussa Nedjari 17/10/2016 17:00
		for(String idAlbum : listIdAlbum){
			insertIdAlbumIfNotExist(idAlbum); // On insère l'id dans l'album dans la collection Albums
			ArrayList<MFMusic> listMusic = new ArrayList<MFMusic>(mapAlbumIdWithAlbum.get(idAlbum));
			for(MFMusic mf : listMusic){
				idArtist = mf.getArtistId();
				nameArtist = mf.getArtistName();
				// ajout de l'artist dans la base mongo Artists 
				//( test de présence de l'artiste déjà effectué dans la méthode appellé)
				insertArtistIfNotExist(nameArtist, idArtist);
				// Pour chaque MFMusic présentes dans l'album
				MFLyrics mfL = mf.getLyrics();
				// on récupère les lyrics
				// et on insère dans la base mongo Lyrics
				insertLyricsIfNotExists(mfL.getLyricsBody(), mf.getTrackId(),
						mf.getArtistId(), mf.getTrackName(),mfL.getLyrics_language(), 
						mf.getTrackSpotifyId(), mf.getTrackSoundcloudId());

				// Début de la création des tags pour chaque lyrics
				// TODO JULIEN : rajouter mfL.getLyrics_language()
				// comme deuxième paramètre du parser
				for( String tag : ParserUtils.parserProcess(mfL.getLyricsBody()) ) {
					tag = tag.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
					insertTagIfNotExists(tag, mf.getTrackId());
				}
			}
		}
	}

	//TODO : Modifier les noms d'attributs (collection, collection1, etc...), nettoyer le code
	// FAIT : Moussa Nedjari 17/10/2106 16:53 (ajout du test du cursor = null si jamais mot de l'user
	// non présent dans la base mongo des tags
	@SuppressWarnings("unchecked")
	public ArrayList<MusicDTO> searchMusicsByTagsInTags(ArrayList<String> tags){
		HashMap<String,Integer> mapIdMusicNbOccurTag = new HashMap<String,Integer>();
		ArrayList<MusicDTO> listMusic = new ArrayList<MusicDTO>();
		MongoCollection<Document> collection_Tags = getCollection(MongoCollections.TAGS);
		List<MongoCursor<Document>> listCursor_Tags = new ArrayList<MongoCursor<Document>>();

		for(String s : tags){
			MongoCursor<Document> cursor_tags = findBy(collection_Tags, new Document("$eq",s));
			if(cursor_tags != null)
				listCursor_Tags.add(cursor_tags);
		}

		for(MongoCursor<Document> cursor : listCursor_Tags){
			Document doc_tags = cursor.next();
			List<String> listIdMusic = (List<String>)doc_tags.get("idMusic");
			for(String id : listIdMusic){
				if(mapIdMusicNbOccurTag.get(id).equals(null)){
					mapIdMusicNbOccurTag.put(id, 1);
				}
				else{
					mapIdMusicNbOccurTag.replace(id, mapIdMusicNbOccurTag.get(id)+1);
				}
			}
		}

		Set<String> list = mapIdMusicNbOccurTag.keySet();
		ArrayList<String> idList = new ArrayList<String>(list);
		ArrayList<Integer> scoreList = (ArrayList<Integer>) mapIdMusicNbOccurTag.values();

		ArrayList<Integer> newList = MathUtils.getNbIdMaxOfList(scoreList, scoreList.size());
		MongoCollection<Document> collection_Musics = getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = getCollection(MongoCollections.ARTISTS);
		MusicDTO msDto;
		for(int i : newList){
			String id = idList.get(i);
			MongoCursor<Document> cursor_Musics = findBy(collection_Musics,new Document("$eq",id));
			MongoCursor<Document> cursor_Artists = findBy(collection_Artists,new Document("$eq",id));
			Document doc_Artists = cursor_Artists.next();
			String nameArtist = doc_Artists.getString("nameArtist");
			Document doc_Musics = cursor_Musics.next();
			msDto = new MusicDTO(doc_Musics.getString("nameMusic"),nameArtist,doc_Musics.getString("spotifyId"),
					doc_Musics.getString("soundcloudId"));
			listMusic.add(msDto);
		}
		return listMusic;
	}


	public void close(){
		mongoClient.close();
	}

}
