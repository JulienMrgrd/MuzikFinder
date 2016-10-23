package utils;

public final class MuzikFinderPreferences {

	// Preferences
	public static final Integer MAX_TOP_TRACKS = 100;
	public static final int PAGE_MAX = 100;
	public static final String[] COUNTRY_ORDER = {"us", "gb", "fr", "it", "es", "aus", "br"};
	
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
