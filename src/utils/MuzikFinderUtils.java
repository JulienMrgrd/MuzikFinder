package utils;

import javax.servlet.http.Cookie;

public final class MuzikFinderUtils {
	
	public static String generateRandomIdSearch(String username){
		if(username==null) username = "";
		long random = (long) (Math.random()*1000L+1);
		return username+random;
	}
	
	public static String getCookieValueByName(String name, Cookie[] cookies){
		if (cookies != null) {
		    for(Cookie cookie : cookies) {
		        if(cookie.equals(cookie.getName())) return cookie.getValue();
		    }
		}
		return null;
	}
	
}
