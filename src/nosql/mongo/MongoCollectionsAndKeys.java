package nosql.mongo;

public final class MongoCollectionsAndKeys {

	private MongoCollectionsAndKeys(){ }

	public static final String TAGS = "Tags";
	public static final String MUSICS = "Musics";
	public static final String ALBUMS = "Albums";
	public static final String PREFS = "Preferences";
	public static final String SEARCH = "Search";
	public static final String CACHE = "Cache";
	public static final String STATS = "Stats";
	public static final String STATS_CACHE = "Stats_Cache";

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

	/// Collection STATS
	public static final String DATEWEEKSYEARS_STATS = "weeks";
	/** [0-17] = -18 ; [18-24] = -25 ; [25-49] = -50 ; [50+] = +50 **/
	public static final String AGERANGE_STATS = "ageRanges";
	public static final String AGE_STATS = "age";
	public static final String MUSICS_STATS = "musics";
	public static final String MINUSEIGHTEEN_STATS = "-18";
	public static final String MINUSTWENTYFIVE_STATS = "-25";
	public static final String MINUSFIFTY_STATS = "-50";
	public static final String PLUSFIFTY_STATS = "+50";
	public static final String IDMUSIC_STATS = "musicId";
	public static final String SCOREMUSIC_STATS = "score";
	//public static final String ID_MUSIC_SCORE_SEARCH = "musicsScore";
	/// Collection SEARCH
	
	/// Collection STATS_CACHE
	public static final String DATEWEEKSYEARS_STATS_CACHE = "weeks";
	/** [0-17] = -18 ; [18-24] = -25 ; [25-49] = -50 ; [50+] = +50 **/
	public static final String AGERANGE_STATS_CACHE = "ageRanges";
	public static final String AGE_STATS_CACHE = "age";
	public static final String MUSICS_STATS_CACHE = "musics";
	public static final String MUSIC_ID_STATS_CACHE = "musicId";
	//TODO:
}
