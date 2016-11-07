package utils;

public final class MuzikFinderUtils {
	
	public static String generateRandomIdSearch(String username){
		if(username==null) username = "";
		long random = (long) (Math.random()*1000L+1);
		return username+random;
	}
	
}
