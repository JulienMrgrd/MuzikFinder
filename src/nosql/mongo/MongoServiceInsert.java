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

	public static boolean insertLyricsIfNotExists(String words, String musicId, String artistId, String artistName, 
			String nameMusic, String langue, String spotifyId, String soundCloudId, MongoService ms){
		if(ms.containsLyrics(musicId)) return false; 

		MongoCollection<Document> collection = ms.getCollection(MongoCollections.MUSICS);
		Document doc = new Document();
		if(musicId!=null) doc.put("idMusic",musicId);
		if(words!=null) doc.put("lyrics",words);
		if(artistId!=null) doc.put("idArtist",artistId);
		if(artistName!=null) doc.put("artistName",artistName);
		if(nameMusic!=null) doc.put("nameMusic", nameMusic);
		if(langue!=null) doc.put("langue", langue);
		if(spotifyId!=null) doc.put("spotifyId", spotifyId);
		if(soundCloudId!=null) doc.put("soundCloudId",soundCloudId);
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
				for(MFMusic mf : listMusic){
					// Pour chaque MFMusic présentes dans l'album
					MFLyrics mfL = mf.getLyrics();
					if(mfL != null){
						// delete last MusixMatch characters
						String lyrics = mfL.getLyricsBody().substring(0, mfL.getLyricsBody().length()-MusixMatchUtils.SIZE_OF_LYRICS_END);

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
			
			cpt++;
			System.out.println("Insertion "+cpt+"/"+listIdAlbum.size()+" OK.");
		}
	}

	public static void insertCacheSearchUser(List<String> tags, List<String> idMusics, MongoService ms, String idRecherche){
		Document doc;
		MongoCollection<Document> collection = ms.getCollection(MongoCollections.CACHE);
		System.out.println("tags =="+tags);
		if(ms.containsIdRecherche(idRecherche)){
			doc = new Document("idRecherche", new Document("$eq",idRecherche));
			MongoCursor<Document> cursor = ms.findBy(collection, doc);
			if(cursor.hasNext()){
				Document doc1 = cursor.next();
				Document doc2;
				doc2 = new Document(new Document("$set",new Document("tags",tags)));
				ms.updateOne(collection, doc1,doc2);
				doc2 = new Document(new Document("$set",new Document("idMusics",idMusics)));
				ms.updateOne(collection, doc1,doc2);
			}
		}else{
			doc = new Document();
			doc.put("tags", tags);
			doc.put("idRecherche",idRecherche);
			doc.put("idMusics", idMusics);
			doc.put("time",new Date().getTime());
			ms.insertOne(collection, doc);
		}
		System.out.println("Nombre de musiques ajoutées dans la collection Cache = "+idMusics.size());
	}
}
