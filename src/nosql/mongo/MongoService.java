package nosql.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import utils.IdMusicScore;
import utils.textMining.ParserMaison;

public class MongoService {

	ServerAddress serverAddress;
	MongoCredential mongoCredential;
	MongoClient mongoClient;
	MongoDatabase db;

	public MongoService(boolean activeAllLogs) {
		if(activeAllLogs){
			Logger.getLogger( "org.mongodb.driver" ).setLevel(Level.WARNING); // e.g. or Log.WARNING, etc.
		}

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
	
	private Document findFirst(MongoCollection<Document> collection){
		try {
			return collection.find().limit(1).first();
		} catch (Exception e){
			return null;
		}
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
		} else if(containsIdMusicInTag(tag,musicId)){
			//System.out.println("IdMusic already corresponding in the Tag Collection\n");
			return false;
		} else {
			doc = new Document("tag", new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = findBy(collection, doc);
			if(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				List<String> listeId = (List<String>) doc1.get("idMusic");
				List<String> newListeId = new ArrayList<String>();
				newListeId.addAll(listeId);
				newListeId.add(musicId);
				doc2 = new Document(new Document("$set",new Document("idMusic",newListeId)));
				updateOne(collection, doc1,doc2);
			}
			return true;
		}
	}


	public boolean insertLyricsIfNotExists(String words, String musicId, String artistId, 
			String nameMusic, String langue, String spotifyId, String soundCloudId){
		if(containsLyrics(musicId)) return false; 

		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS);
		Document doc = new Document();
		if(musicId!=null) doc.put("idMusic",musicId);
		if(words!=null) doc.put("lyrics",words);
		if(artistId!=null) doc.put("idArtist",artistId);
		if(nameMusic!=null) doc.put("nameMusic", nameMusic);
		if(langue!=null) doc.put("langue", langue);
		if(spotifyId!=null) doc.put("spotifyId", spotifyId);
		if(soundCloudId!=null) doc.put("soundCloudId",soundCloudId);
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
		return cursor.hasNext();
	}

	public boolean containsLyrics(String musicId){
		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document doc = new Document("idMusic", new Document("$eq",musicId)); // crée le document retournant les informations pr�sentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		return cursor.hasNext();
	}

	public boolean containsTag(String tag){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS);
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = findBy(collection, findQuery);
		return cursor.hasNext();
	}

	@SuppressWarnings("unchecked")
	public boolean containsIdMusicInTag(String tag, String idMusic){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS); // récupère la collection mongo qui stocke les tags

		Document doc = new Document("tag",new Document("$regex",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
		MongoCursor<Document> cursor = findBy(collection, doc);
		
		Document doc_new;
		List<String> listIdMusic;
		
		while(cursor.hasNext()){
			doc_new = cursor.next();
			listIdMusic = (List<String>) doc_new.get("idMusic");
			for( String s : listIdMusic ){
				return s.equals(idMusic);
			}
		}
		return false;
	}

	public boolean containsIdAlbum(String idAlbum){
		MongoCollection<Document> collection = getCollection(MongoCollections.ALBUMS);
		Document doc = new Document("idAlbum",new Document("$eq",idAlbum));
		MongoCursor<Document> cursor = findBy(collection, doc);

		return cursor.hasNext();
	}

	@SuppressWarnings("unchecked")
	public List<String> getIdMusicsByTag(String tag){
		MongoCollection<Document> collection = getCollection(MongoCollections.TAGS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("tag", new Document("$eq",tag));
		MongoCursor<Document> cursor = findBy(collection, findQuery);
		
		List<String> listIdMusic = null;
		if(cursor.hasNext()){
			listIdMusic = (ArrayList<String>) cursor.next().get("idMusic");
		}
		return listIdMusic;
	}

	public List<String> getIdMusicsByIdArtist(String idArtist){
		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("idArtist", new Document("$eq",idArtist));
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		List<String> listeId = new ArrayList<String>();
		Document doc;
		while(cursor.hasNext()){
			doc = cursor.next();
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


	public List<String> getIdMusicsByChainWords(String chainWords){
		MongoCollection<Document> collection = getCollection(MongoCollections.MUSICS); // récupère la collection mongo qui stocke les musiques
		Document findQuery = new Document("lyrics", new Document("$regex",chainWords));
		MongoCursor<Document> cursor = findBy(collection, findQuery);

		ArrayList<String> listeId = new ArrayList<String>();
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


	public void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum) throws Exception {
		Set<String> listIdAlbum = mapAlbumIdWithAlbum.keySet();
		ArrayList<MFMusic> listMusic;
		
		int cpt = 0;
		System.out.println("Début de l'insertion des albums (et tout ce qui s'y rattache) en base");
		for(String idAlbum : listIdAlbum){
			insertIdAlbumIfNotExist(idAlbum); // On insère l'id dans l'album dans la collection Albums
			listMusic = new ArrayList<MFMusic>(mapAlbumIdWithAlbum.get(idAlbum));
			
			// ajout de l'artist dans la base mongo Artists 
			//(test de présence de l'artiste déjà effectué dans la méthode appelée)
			if(!listMusic.isEmpty()){
				insertArtistIfNotExist(listMusic.get(0).getArtistId(), listMusic.get(0).getArtistName());
				
				for(MFMusic mf : listMusic){
					// Pour chaque MFMusic présentes dans l'album
					MFLyrics mfL = mf.getLyrics();
					if(mfL != null){
						// on récupère les lyrics et on insère dans la base mongo Lyrics
						insertLyricsIfNotExists(mfL.getLyricsBody(), mf.getTrackId(),
								mf.getArtistId(), mf.getTrackName(),mfL.getLyrics_language(), 
								mf.getTrackSpotifyId(), mf.getTrackSoundcloudId());
						
						// Début de la création des tags pour chaque lyrics
						List<String> tags = ParserMaison.parserProcess(mfL.getLyricsBody(), mfL.getLyrics_language());
						if(tags != null && !tags.isEmpty()){
							for( String tag : tags ) {
								insertTagIfNotExists(tag, mf.getTrackId());
							}
						}
					}
				}
			}
			
			cpt++;
			System.out.println("Insertion "+cpt+"/"+listIdAlbum.size()+" OK.");
		}
	}
	
	public List<MusicDTO> searchMusicsByTagsInTags(List<String> tags){
		List<IdMusicScore> idMusicScore = new ArrayList<>();
		List<String> listIdMusics = new ArrayList<>();

		for(String tag : tags){
			listIdMusics = this.getIdMusicsByTag(tag);
			for(String idMusic : listIdMusics){
				for(IdMusicScore musicScore : idMusicScore){
					if(musicScore.getIdMusic().equals(idMusic)){
						musicScore.incrementScore();
					} else {
						idMusicScore.add(new IdMusicScore(idMusic, 1));
					}
				}
			}
		}
		
		Collections.sort(idMusicScore);
		MusicDTO msDto;
		List<MusicDTO> listMusic= new ArrayList<MusicDTO>(idMusicScore.size());

		MongoCollection<Document> collection_Musics = getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = getCollection(MongoCollections.ARTISTS);
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		MongoCursor<Document> cursor_Musics, cursor_Artists;
		Document doc_Musics, doc_Artists, findQuery_Artists, findQuery_MusicByIdMusic;
		
		for(IdMusicScore ms : idMusicScore){
			findQuery_MusicByIdMusic = new Document("idMusic", new Document("$eq",ms.getIdMusic()));
			cursor_Musics = findBy(collection_Musics, findQuery_MusicByIdMusic);
			
			if(cursor_Musics.hasNext()){ // On récupere l'ensemble du document dans Musics faisant 
				doc_Musics = cursor_Musics.next(); // reference a la musique avec l'id ms.getIdMusic
				
				//On recupere l'identifiant de l'artiste ayant fait la musique
				findQuery_Artists = new Document("idArtist", new Document("$eq",doc_Musics.getString("idArtist")));
				cursor_Artists = findBy(collection_Artists,findQuery_Artists);
				String nameArtist = "";
				
				if(cursor_Artists.hasNext()){
					doc_Artists = cursor_Artists.next();
					nameArtist=doc_Artists.getString("nameArtist");
				}
				msDto = new MusicDTO(ms.getIdMusic(), doc_Musics.getString("nameMusic"), doc_Musics.getString("idArtist"),
						nameArtist, "", doc_Musics.getString("spotifyId"), doc_Musics.getString("soundcloudId"));
				listMusic.add(msDto);
				
			}
		}
		
		return listMusic;
	}
	
	//TODO : A supprimer non ?
//	@SuppressWarnings("unchecked")
//	public List<MusicDTO> searchMusicsByTagsInTagsOld(List<String> tags){
//		HashMap<String,Integer> mapIdMusicNbOccurTag = new HashMap<String,Integer>();
//		ArrayList<MusicDTO> listMusic = new ArrayList<MusicDTO>();
//		MongoCollection<Document> collection_Tags = getCollection(MongoCollections.TAGS);
//		List<MongoCursor<Document>> listCursor_Tags = new ArrayList<MongoCursor<Document>>();
//
//		for(String s : tags){
//			Document findQuery = new Document("tag", new Document("$eq",s));
//			MongoCursor<Document> cursor_tags = findBy(collection_Tags, findQuery);
//			if(cursor_tags != null)
//				listCursor_Tags.add(cursor_tags);
//		}
//
//		for(MongoCursor<Document> cursor : listCursor_Tags){
//			if(cursor.hasNext()){
//				Document doc_tags = cursor.next();
//				List<String> listIdMusic = (List<String>)doc_tags.get("idMusic");
//				for(String id : listIdMusic){
//					if(mapIdMusicNbOccurTag.get(id)==null){
//						mapIdMusicNbOccurTag.put(id, 1);
//					}
//					else{
//						mapIdMusicNbOccurTag.replace(id, mapIdMusicNbOccurTag.get(id)+1);
//					}
//				}
//			}
//		}
//
//		Set<String> list = mapIdMusicNbOccurTag.keySet();
//		ArrayList<String> idList = new ArrayList<String>(list);
//		ArrayList<Integer> scoreList = new ArrayList<Integer>((HashSet<Integer>)mapIdMusicNbOccurTag.values());
//
//		ArrayList<Integer> newList = MathUtils.getNbIdMaxOfList(scoreList, scoreList.size());
//		MongoCollection<Document> collection_Musics = getCollection(MongoCollections.MUSICS);
//		MongoCollection<Document> collection_Artists = getCollection(MongoCollections.ARTISTS);
//		MusicDTO msDto;
//		for(int i : newList){
//			String id = idList.get(i);
//			Document findQuery_Musics = new Document("idArtist", new Document("$eq",id));
//			Document findQuery_Artists = new Document("idArtist", new Document("$eq",id));
//			MongoCursor<Document> cursor_Musics = findBy(collection_Musics,findQuery_Musics);
//			MongoCursor<Document> cursor_Artists = findBy(collection_Artists,findQuery_Artists);
//			Document doc_Artists = cursor_Artists.next();
//			String nameArtist = doc_Artists.getString("nameArtist");
//			Document doc_Musics = cursor_Musics.next();
//			msDto = new MusicDTO(doc_Musics.getString("idMusic"), doc_Musics.getString("nameMusic"), doc_Musics.getString("idArtist")
//					, nameArtist,
//					"", //albumId
//					doc_Musics.getString("spotifyId"), doc_Musics.getString("soundcloudId"));
//			listMusic.add(msDto);
//		}
//		return listMusic;
//	}

	/**
	 * Cette méthode permet de chercher les musics correspondantes au tags entrés par
	 * l'utilisateur en traitant les tags comme une phrase complète
	 * @param tags
	 * @return
	 */
	public List<String> matchMusicsWithTags(List<String> tags){
		ArrayList<String> result = new ArrayList<String>();
		String words = tags.toString();
		MongoCollection<Document> collection_Musics = getCollection(MongoCollections.MUSICS);
		MongoCursor<Document> cursor_Music = findBy(collection_Musics, new Document());
		
		// On sors les variables temporaires du for pour utiliser efficacement l'espace mémoire
		Document doc_lyrics;
		String lyrics;
		while(cursor_Music.hasNext()){
			doc_lyrics = cursor_Music.next();
			lyrics = doc_lyrics.getString("lyrics");
			if(lyrics.contains(words)) result.add(doc_lyrics.getString("nameMusic"));
		}
		return result;
	}

	public List<MusicDTO> searchMusicsByTagsInLyrics(List<String> tags){
		List<MusicDTO> listMusics = new ArrayList<MusicDTO>();
		List<String> list_NameMusics = matchMusicsWithTags(tags);
		MusicDTO mDto;

		MongoCollection<Document> collection_Musics = getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = getCollection(MongoCollections.ARTISTS);
		MongoCursor<Document> cursor_Music; 
		MongoCursor<Document> cursor_Artist; 
		
		Document findQuery_Musics;
		Document doc_Music;
		String idArtist;
		Document findQuery_Artists;
		Document doc_Artist;

		for(String nameMusic : list_NameMusics){
			findQuery_Musics = new Document("nameMusic", new Document("$eq",nameMusic));			
			cursor_Music = findBy(collection_Musics,findQuery_Musics);
			doc_Music = cursor_Music.next();
			idArtist = doc_Music.getString("idArtist");
			findQuery_Artists = new Document("idArtist", new Document("$eq",idArtist));
			cursor_Artist = findBy(collection_Artists,findQuery_Artists);
			doc_Artist = cursor_Artist.next();
			mDto = new MusicDTO(doc_Music.getString("idMusic"), nameMusic, idArtist, doc_Artist.getString("nameArtist"),
					"", //albumId
					doc_Music.getString("spotifyId"), doc_Music.getString("soundcloudId"));

			listMusics.add(mDto);
		}

		return listMusics;
	}

	public List<MusicDTO> searchMusicsByTagsWithLyrics(List<String> tags){
		int cptWordsInLyrics = 0;
		ArrayList<MusicDTO> listMusic = new ArrayList<MusicDTO>();
		
		MongoCollection<Document> collection_Musics = getCollection(MongoCollections.MUSICS);
		MongoCollection<Document> collection_Artists = getCollection(MongoCollections.ARTISTS);
		
		Document findQuery_Musics = new Document("lyrics",new Document());
		Document findQuery_Artists;
		
		MongoCursor<Document> cursor_Music = findBy(collection_Musics,findQuery_Musics);
		MongoCursor<Document> cursor_Artist;
		
		MusicDTO mDto;
		while(cursor_Music.hasNext()){
			cptWordsInLyrics = 0;
			Document doc_Music = cursor_Music.next();
			String lyrics = doc_Music.getString("lyrics");
			for(String s : tags){
				if(lyrics.contains(s)) cptWordsInLyrics++;
			}
			if(cptWordsInLyrics > (tags.size()/2)){
				String idArtist = doc_Music.getString("idArtist");
				findQuery_Artists = new Document("idArtist", new Document("$eq",idArtist));
				cursor_Artist = findBy(collection_Artists,findQuery_Artists);
				Document doc_Artist = cursor_Artist.next();
				mDto = new MusicDTO(doc_Music.getString("idMusic"), doc_Music.getString("nameMusic"), idArtist, doc_Artist.getString("nameArtist"),
						"", //albumId
						doc_Music.getString("spotifyId"), doc_Music.getString("soundcloudId"));
				listMusic.add(mDto);
			}
		}

		return listMusic;
	}

	public void close(){
		mongoClient.close();
	}

	public void setLastCountryPref(int pos) {
		MongoCollection<Document> collection = getCollection(MongoCollections.PREFS);
		Document oldPref = findFirst(collection);
		if(oldPref != null) {
			Document newPref = new Document("$set",new Document("posCountry",pos));
			updateOne(collection, oldPref, newPref);
		} else {
			Document newPref = new Document("posCountry",pos);
			insertOne(collection, newPref);
		}
	}

	public int getLastCountryPref() {
		try {
			MongoCollection<Document> collection = getCollection(MongoCollections.PREFS);
			Document doc = findFirst(collection);
			return doc.getInteger("posCountry", 0);
		} catch (Exception e){
			return 0;
		}
	}
	
}