package nosql.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import interfaces.MFLyrics;
import interfaces.MFMusic;
import utils.textMining.ParserMaison;

public class MongoServiceInsert {

	@SuppressWarnings("unchecked")
	public static boolean insertTagIfNotExists(String tag, String musicId, MongoService ms){
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.TAGS);
		Document doc;
		if(!ms.containsTag(tag)){
			doc = new Document();
			doc.put("tag",tag);
			List<String> listId = new ArrayList<String>(1);
			listId.add(musicId);
			doc.put("idMusic", listId);
			ms.insertOne(collection, doc);
			return true;
		} else if(ms.containsIdMusicInTag(tag,musicId)){
			//System.out.println("IdMusic already corresponding in the Tag Collection\n");
			return false;
		} else {
			doc = new Document("tag", new Document("$eq",tag)); // crée le document retournant les informations présentes dans la collection lyrics correspondantes
			MongoCursor<Document> cursor = ms.findBy(collection, doc);
			if(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				List<String> listeId = (List<String>) doc1.get("idMusic");
				List<String> newListeId = new ArrayList<String>();
				newListeId.addAll(listeId);
				newListeId.add(musicId);
				doc2 = new Document(new Document("$set",new Document("idMusic",newListeId)));
				ms.updateOne(collection, doc1,doc2);
			}
			return true;
		}
	}

	public static boolean insertLyricsIfNotExists(String words, String musicId, String artistId, 
			String nameMusic, String langue, String spotifyId, String soundCloudId, MongoService ms){
		if(ms.containsLyrics(musicId)) return false; 

		MongoCollection<Document> collection = ms.getCollection(MongoCollections.MUSICS);
		Document doc = new Document();
		if(musicId!=null) doc.put("idMusic",musicId);
		if(words!=null) doc.put("lyrics",words);
		if(artistId!=null) doc.put("idArtist",artistId);
		if(nameMusic!=null) doc.put("nameMusic", nameMusic);
		if(langue!=null) doc.put("langue", langue);
		if(spotifyId!=null) doc.put("spotifyId", spotifyId);
		if(soundCloudId!=null) doc.put("soundCloudId",soundCloudId);
		ms.insertOne(collection, doc);
		return true;
	}

	public static boolean insertArtistIfNotExist(String artistName, String artistId, MongoService ms){
		if(ms.containsArtist(artistId)) return false;

		MongoCollection<Document> collection = ms.getCollection(MongoCollections.ARTISTS); // récupère la collection mongo qui stocke les artistes
		Document doc = new Document();
		doc.put("idArtist", artistId);
		doc.put("nameArtist",artistName);
		ms.insertOne(collection, doc);
		return true;
	}

	public static boolean insertIdAlbumIfNotExist(String idAlbum, MongoService ms){
		if(ms.containsIdAlbum(idAlbum)) return false;

		MongoCollection<Document> collection = ms.getCollection(MongoCollections.ALBUMS);
		Document doc = new Document();
		doc.put("idAlbum", idAlbum);
		ms.insertOne(collection,doc);
		return true;
	}

	public static void insertNewMusics(Map<String, List<MFMusic>> mapAlbumIdWithAlbum, MongoService ms){
		Set<String> listIdAlbum = mapAlbumIdWithAlbum.keySet();
		ArrayList<MFMusic> listMusic;
		
		int cpt = 0;
		System.out.println("Début de l'insertion des albums (et tout ce qui s'y rattache) en base");
		for(String idAlbum : listIdAlbum){
			ms.insertIdAlbumIfNotExist(idAlbum); // On insère l'id dans l'album dans la collection Albums
			listMusic = new ArrayList<MFMusic>(mapAlbumIdWithAlbum.get(idAlbum));
			
			// ajout de l'artist dans la base mongo Artists 
			//(test de présence de l'artiste déjà effectué dans la méthode appelée)
			if(!listMusic.isEmpty()){
				ms.insertArtistIfNotExist(listMusic.get(0).getArtistName(), listMusic.get(0).getArtistId());
				
				for(MFMusic mf : listMusic){
					// Pour chaque MFMusic présentes dans l'album
					MFLyrics mfL = mf.getLyrics();
					if(mfL != null){
						// on récupère les lyrics et on insère dans la base mongo Lyrics
						ms.insertLyricsIfNotExists(mfL.getLyricsBody(), mf.getTrackId(),
								mf.getArtistId(), mf.getTrackName(),mfL.getLyrics_language(), 
								mf.getTrackSpotifyId(), mf.getTrackSoundcloudId());
						
						// Début de la création des tags pour chaque lyrics
						List<String> tags = ParserMaison.parserProcess(mfL.getLyricsBody(), mfL.getLyrics_language());
						if(tags != null && !tags.isEmpty()){
							for( String tag : tags ) {
								ms.insertTagIfNotExists(tag, mf.getTrackId());
							}
						}
					}
				}
			}
			
			cpt++;
			System.out.println("Insertion "+cpt+"/"+listIdAlbum.size()+" OK.");
		}
	}

	public static void insertCacheSearchUser(List<String> idMusics, MongoService ms, String idRecherche){
		Document doc;
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.CACHE);
		if(ms.containsIdRecherche(idRecherche)){
			doc = new Document("idRecherche", new Document("$eq",idRecherche));
			MongoCursor<Document> cursor = ms.findBy(collection, doc);
			if(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				doc2 = new Document(new Document("$set",new Document("idMusics",idMusics)));
				ms.updateOne(collection, doc1,doc2);
			}
		}else{
			doc = new Document();
			doc.put("idRecherche",idRecherche);
			doc.put("idMusics", idMusics);
			ms.insertOne(collection, doc);
		}
		System.out.println("nombre de musique ajouté dans la collection cache = "+idMusics.size());
	}
}
