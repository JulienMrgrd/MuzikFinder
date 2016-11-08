package nosql.mongo;

import org.bson.Document;

import interfaces.MFMusic;
import server.dto.MFLyricsDTO;
import server.dto.MFMusicDTO;

public final class MongoUtils {
	
	private MongoUtils(){}
	
	/**
	 * Transform Document from the MUSICS collection into a MFMusic
	 * @param music
	 * @return MFMusic
	 */
	public static MFMusic transformDocumentIntoMFMusic(Document music){
		try {
			return new MFMusicDTO(music.getString(MongoCollectionsAndKeys.IDMUSIC_MUSICS), 
					music.getString(MongoCollectionsAndKeys.MUSICNAME_MUSICS), 
					music.getString(MongoCollectionsAndKeys.ARTISTID_MUSICS), 
					music.getString(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS), 
					music.getString(MongoCollectionsAndKeys.ALBUMID_MUSICS),
					music.getString(MongoCollectionsAndKeys.ALBUMNAME_MUSICS),
					music.getString(MongoCollectionsAndKeys.SPOTIFYID_MUSICS), 
					music.getString(MongoCollectionsAndKeys.SOUNDCLOUDID_MUSICS), 
					"",
					new MFLyricsDTO(music.getString(MongoCollectionsAndKeys.LYRICS_MUSICS), 
									music.getString(MongoCollectionsAndKeys.LANGUAGE_MUSICS)),
					music.getString(MongoCollectionsAndKeys.MUSICGENRE_MUSICS));
		} catch (NullPointerException e){
			System.out.println("transformDocumentIntoMFMusic error : "+e);
			return null;
		}
	}

}
