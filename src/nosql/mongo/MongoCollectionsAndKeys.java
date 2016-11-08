package nosql.mongo;

public final class MongoCollectionsAndKeys {

	private MongoCollectionsAndKeys(){ }

	public static final String TAGS = "Tags";
	public static final String MUSICS = "Musics";
	public static final String ALBUMS = "Albums";
	public static final String PREFS = "Preferences";
	public static final String SEARCH = "Search";
	public static final String CACHE = "Cache";

	/// Collection ALBUMS
	public static final String IDALBUM_ALBUMS = "idAlbum";

	/// Collection MUSICS
	public static final String IDMUSIC_MUSICS = "idMusic";
	public static final String LYRICS_MUSICS = "lyrics";
	public static final String IDARTIST_MUSICS = "idArtist";
	public static final String ARTISTSNAME_MUSICS = "artistName";
	public static final String NAMEMUSIC_MUSICS = "nameMusic";
	public static final String LANGUAGE_MUSICS = "language";
	public static final String SPOTIFYID_MUSICS = "spotifyId";
	public static final String SOUNDCLOUDID_MUSICS = "soundCloudId";

	/// Collection TAGS
	public static final String TAG_TAGS = "tag";
	public static final String IDMUSIC_TAGS = "idMusic";

	/// Collection CACHE
	public static final String TAGS_CACHE = "tags";
	public static final String SEARCHID_CACHE = "searchId";
	public static final String IDMUSICS_CACHE = "idMusics";
	public static final String TIME_CACHE = "time";
	
	/// Collection PREFERENCES
	public static final String POSCOUNTRY_PREFS = "posCountry";
	
	/// Collection SEARCH
	//TODO:
}
