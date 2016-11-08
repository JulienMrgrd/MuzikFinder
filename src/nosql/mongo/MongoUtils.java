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
					music.getString(MongoCollectionsAndKeys.NAMEMUSIC_MUSICS), 
					music.getString(MongoCollectionsAndKeys.IDARTIST_MUSICS), 
					music.getString(MongoCollectionsAndKeys.ARTISTSNAME_MUSICS), 
					"", 
					music.getString(MongoCollectionsAndKeys.SPOTIFYID_MUSICS), 
					music.getString(MongoCollectionsAndKeys.SOUNDCLOUDID_MUSICS), 
					"", 
					new MFLyricsDTO(music.getString(MongoCollectionsAndKeys.LYRICS_MUSICS), 
									music.getString(MongoCollectionsAndKeys.LANGUAGE_MUSICS)));
		} catch (NullPointerException e){
			return null;
		}
	}

}
