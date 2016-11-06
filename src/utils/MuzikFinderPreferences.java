package utils;

public final class MuzikFinderPreferences {

	// Preferences
	public static final Integer MAX_TOP_TRACKS = 100;
	public static final int PAGE_MAX = 100;
	public static final int MIN_SIZE_OF_USERNAME = 5;
	public static final int MIN_SIZE_OF_PASSWORD = 5;
	
	public static final String POS_COUNTRY_PREF = "posCountry";
	public static final String[] COUNTRY_ORDER = {"us", "gb", "au", "fr", "ca", "be"};
	public static final int[] PREF_NB_MUSIC_FILTER= {50,20};
	public static final String MUSIXMATCH_KEY_PREF = "musixmatch_pos";
	public static final int LIMITACCEPTABLETEMPS = 15;
	public static final boolean LOGS = false;
	
	public static int[] getPrefNbMusicFilter() {
		return PREF_NB_MUSIC_FILTER;
	}

	private MuzikFinderPreferences(){ }

	public static String getCountry(int pos) {
		if(pos>(COUNTRY_ORDER.length-1)){
			return COUNTRY_ORDER[0];
		}
		return COUNTRY_ORDER[pos];
	}
	
	public static int getNextPosition(int lastPos){
		return (lastPos+1) % COUNTRY_ORDER.length;
	}
	
}
