package nosql.mongo;

public final class MongoCollectionsAndKeys {

	private MongoCollectionsAndKeys(){ }

	public static final String TAGS = "Tags";
	public static final String MUSICS = "Musics";
	public static final String ALBUMS = "Albums";
	public static final String PREFS = "Preferences";
	public static final String SEARCH = "Search";
	public static final String CACHE = "Cache";
	public static final String STAT = "Statistique";

	/// Collection ALBUMS
	public static final String ALBUMID_ALBUMS = "albumId";

	/// Collection MUSICS
	public static final String IDMUSIC_MUSICS = "musicId";
	public static final String LYRICS_MUSICS = "lyrics";
	public static final String ARTISTID_MUSICS = "artistId";
	public static final String ARTISTSNAME_MUSICS = "artistName";
	public static final String ALBUMID_MUSICS = "albumId";
	public static final String ALBUMNAME_MUSICS = "albumName";
	public static final String MUSICNAME_MUSICS = "musicName";
	public static final String LANGUAGE_MUSICS = "language";
	public static final String SPOTIFYID_MUSICS = "spotifyId";
	public static final String SOUNDCLOUDID_MUSICS = "soundCloudId";
	public static final String MUSICGENRE_MUSICS = "musicGenre";

	/// Collection TAGS
	public static final String TAG_TAGS = "tag";
	public static final String IDMUSICS_TAGS = "idMusics";
	public static final String MUSICID_TAGS = "musicId";
	public static final String SCORE_TAGS = "score";

	/// Collection CACHE
	public static final String TAGS_CACHE = "tags";
	public static final String SEARCHID_CACHE = "searchId";
	public static final String MUSICSID_CACHE = "musicsId";
	public static final String TIME_CACHE = "time";
	
	/// Collection PREFERENCES
	public static final String POSCOUNTRY_PREFS = "posCountry";
	
	/// Collection SEARCH
	//TODO:
}
