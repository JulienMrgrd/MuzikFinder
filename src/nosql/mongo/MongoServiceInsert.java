package nosql.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import api.musixMatch.utils.MusixMatchUtils;
import interfaces.MFLyrics;
import interfaces.MFMusic;
import utils.IdMusicScore;
import utils.textMining.ParserMaison;

public class MongoServiceInsert {
	
	private static MongoService ms = MongoService.getInstance();

	@SuppressWarnings("unchecked")
	static boolean insertTagIfNotExists(String tag, String musicId){
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.TAGS);
		Document doc;
		if(!ms.containsTag(tag)){
			System.out.println("1er if");
			doc = new Document();
			doc.put(MongoCollectionsAndKeys.TAG_TAGS,tag);

			IdMusicScore ims = new IdMusicScore(musicId, 1);
			List<Document> listDoc=new ArrayList<Document>(1);
			listDoc.add(ims.IdMusicScoreToDoc());
			doc.put(MongoCollectionsAndKeys.MUSICID_TAGS, listDoc);
			ms.insertOne(collection, doc);
		} else if(ms.containsIdMusicInTag(tag,musicId)){
			System.out.println("2eme if");
			doc = new Document(MongoCollectionsAndKeys.TAG_TAGS,new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = ms.findBy(collection, doc);
			
			Document doc_new;
			List<Document> listDocument;
			List<Document> newListDocument = new ArrayList<>();
			String tmpIdMusic;
			while(cursor.hasNext()){
				doc_new = cursor.next();
				listDocument = (List<Document>) doc_new.get(MongoCollectionsAndKeys.MUSICID_TAGS);
				for(Document doc2 : listDocument){
					tmpIdMusic=doc2.getString("idMusic");
					if(tmpIdMusic.equals(musicId)){
						IdMusicScore ims = new IdMusicScore(tmpIdMusic, doc2.getInteger("score")+1);
						newListDocument.add(ims.IdMusicScoreToDoc());
					}else {
						newListDocument.add(doc2);
					}
				}
			}
			Document doc2 = new Document(new Document("$set",new Document(MongoCollectionsAndKeys.MUSICID_TAGS, newListDocument)));
			ms.updateOne(collection, doc,doc2);
		} else {
			System.out.println("3eme if");
			doc = new Document(MongoCollectionsAndKeys.TAG_TAGS, new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = ms.findBy(collection, doc);
			if(cursor.hasNext()){
				System.out.println("in if");
				Document doc1 = cursor.next();
				Document doc2;
				
				List<Document> listDocument = (List<Document>) doc1.get(MongoCollectionsAndKeys.MUSICID_TAGS);
				List<Document> newListDocument = new ArrayList<Document>();
				newListDocument.addAll(listDocument);
				newListDocument.add(new IdMusicScore(musicId, 1).IdMusicScoreToDoc());
				doc2 = new Document(new Document("$set",new Document(MongoCollectionsAndKeys.MUSICID_TAGS, newListDocument)));
				ms.updateOne(collection, doc1,doc2);
			}
		}
		return true;
	}

	static boolean insertLyricsIfNotExists(String words, String musicId, String artistId, String artistName, 
			String nameMusic, String langue, String spotifyId, String soundCloudId){
		if(ms.containsLyrics(musicId)) return false; 

		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.MUSICS);
		Document doc = new Document();
		if(musicId!=null) doc.put(MongoCollectionsAndKeys.IDMUSIC_MUSICS,musicId);
		if(words!=null) doc.put(MongoCollectionsAndKeys.LYRICS_MUSICS,words);
		if(artistId!=null) doc.put(MongoCollectionsAndKeys.ARTISTID_MUSICS,artistId);
		if(artistName!=null) doc.put(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS,artistName);
		if(nameMusic!=null) doc.put(MongoCollectionsAndKeys.MUSICNAME_MUSICS, nameMusic);
		if(langue!=null) doc.put(MongoCollectionsAndKeys.LANGUAGE_MUSICS, langue);
		if(spotifyId!=null) doc.put(MongoCollectionsAndKeys.SPOTIFYID_MUSICS, spotifyId);
		if(soundCloudId!=null) doc.put(MongoCollectionsAndKeys.SOUNDCLOUDID_MUSICS,soundCloudId);
		ms.insertOne(collection, doc);
		return true;
	}

	static boolean insertIdAlbumIfNotExist(String idAlbum){
		if(ms.containsIdAlbum(idAlbum)) return false;

		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.ALBUMS);
		Document doc = new Document();
		doc.put(MongoCollectionsAndKeys.ALBUMID_ALBUMS, idAlbum);
		ms.insertOne(collection,doc);
		return true;
	}

	static void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum){
		Set<String> listIdAlbum = mapAlbumIdWithAlbum.keySet();
		ArrayList<MFMusic> listMusic;
		
		int cpt = 0;
		System.out.println("Début de l'insertion des albums (et tout ce qui s'y rattache) en base");
		for(String idAlbum : listIdAlbum){
			ms.insertIdAlbumIfNotExist(idAlbum); // On insère l'id dans l'album dans la collection Albums
			listMusic = new ArrayList<MFMusic>(mapAlbumIdWithAlbum.get(idAlbum));
			
			// ajout de l'artist dans la base mongo Artists 
			//(test de présence de l'artiste déjà effectué dans la méthode appelante)
			if(!listMusic.isEmpty()){
				for(MFMusic mf : listMusic){
					// Pour chaque MFMusic présentes dans l'album
					MFLyrics mfL = mf.getLyrics();
					if(mfL != null){
						
						String lyrics = mfL.getLyricsBody();
						if(lyrics != null && !lyrics.isEmpty()){
							// delete last MusixMatch characters
							lyrics = mfL.getLyricsBody().substring(0, mfL.getLyricsBody().length()-MusixMatchUtils.SIZE_OF_LYRICS_END);

							// on récupère les lyrics et on insère dans la base mongo Lyrics
							ms.insertLyricsIfNotExists(lyrics, mf.getTrackId(),
									mf.getArtistId(), mf.getArtistName(), mf.getTrackName(),mfL.getLyrics_language(), 
									mf.getTrackSpotifyId(), mf.getTrackSoundcloudId());
							
							// Début de la création des tags pour chaque lyrics
							List<String> tags = ParserMaison.parserProcess(lyrics, mfL.getLyrics_language());
							if(tags != null && !tags.isEmpty()){
								for( String tag : tags ) {
									ms.insertTagIfNotExists(tag, mf.getTrackId());
								}
							}
						}
					}
				}
			}
			
			cpt++;
			System.out.println("Insertion "+cpt+"/"+listIdAlbum.size()+" OK.");
		}
	}

	static void insertCacheSearchUser(List<String> tags, List<String> idMusics, String idRecherche){
		Document doc;
		MongoCollection<Document> collection = ms.getCollection(MongoCollectionsAndKeys.CACHE);
		System.out.println("tags =="+tags);
		if(ms.containsIdRecherche(idRecherche)){
			doc = new Document(MongoCollectionsAndKeys.SEARCHID_CACHE, new Document("$eq",idRecherche));
			MongoCursor<Document> cursor = ms.findBy(collection, doc);
			if(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				doc2 = new Document(new Document("$set",new Document(MongoCollectionsAndKeys.TAGS_CACHE,tags)));
				ms.updateOne(collection, doc1,doc2);
				doc2 = new Document(new Document("$set",new Document(MongoCollectionsAndKeys.MUSICSID_CACHE,idMusics)));
				ms.updateOne(collection, doc1,doc2);
			}
		}else{
			doc = new Document();
			doc.put(MongoCollectionsAndKeys.TAGS_CACHE, tags);
			doc.put(MongoCollectionsAndKeys.SEARCHID_CACHE,idRecherche);
			doc.put(MongoCollectionsAndKeys.MUSICSID_CACHE, idMusics);
			doc.put(MongoCollectionsAndKeys.TIME_CACHE,new Date().getTime());
			ms.insertOne(collection, doc);
		}
		System.out.println("Nombre de musiques ajoutées dans la collection Cache = "+idMusics.size());
	}
	
	public static void main(String[] args){
		insertTagIfNotExists("everybody", "113810490");
	}
}
