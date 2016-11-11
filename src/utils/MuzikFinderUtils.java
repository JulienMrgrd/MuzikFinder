package utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sql.metier.User;

public final class MuzikFinderUtils {
	
	public static String generateRandomIdSearch(String username){
		if(username==null) username = "";
		long random = (long) (Math.random()*1000L+1);
		return username+random;
	}
	
	public static String getCookieValueByName(String name, Cookie[] cookies){
		if (cookies != null && name!=null) {
		    for(Cookie cookie : cookies) {
		        if(name.equals(cookie.getName())) return cookie.getValue();
		    }
		}
		return null;
	}

	public static void createNewCookies(User user, HttpServletResponse response) {
		Cookie userCookie = new Cookie(MuzikFinderPreferences.COOKIE_LOGIN, user.getLogin());
		userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
		userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
		response.addCookie(userCookie);
		
		userCookie = new Cookie(MuzikFinderPreferences.COOKIE_BIRTH, user.getDateBirth().toString()); // for stats
		userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
		userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
		response.addCookie(userCookie);
		
		userCookie = new Cookie(MuzikFinderPreferences.COOKIE_USERID, user.getId()); // for stats
		userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
		userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
		response.addCookie(userCookie);
	}
	
	public static void updateTimeCookies(HttpServletRequest request, HttpServletResponse response) {
		String userLogin = getCookieValueByName(MuzikFinderPreferences.COOKIE_LOGIN, request.getCookies());
		String userId = getCookieValueByName(MuzikFinderPreferences.COOKIE_USERID, request.getCookies());
		String userBirth = getCookieValueByName(MuzikFinderPreferences.COOKIE_BIRTH, request.getCookies());
		updateTimeCookies(userLogin, userId, userBirth, response);
	}
	
	public static void updateTimeCookies(String login, String id, String birth, HttpServletResponse response) {
		if(login==null || id==null || birth==null) return;
		
		Cookie userCookie = new Cookie(MuzikFinderPreferences.COOKIE_LOGIN, login);
		userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
		userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
		response.addCookie(userCookie);
		
		userCookie = new Cookie(MuzikFinderPreferences.COOKIE_BIRTH, birth); // for stats
		userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
		userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
		response.addCookie(userCookie);
		
		userCookie = new Cookie(MuzikFinderPreferences.COOKIE_USERID, id); // for stats
		userCookie.setMaxAge(MuzikFinderPreferences.COOKIE_DURATION); //Store cookie for 1 day
		userCookie.setPath(MuzikFinderPreferences.COOKIE_PATH);
		response.addCookie(userCookie);		
	}
	
}
