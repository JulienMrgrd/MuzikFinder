package api.musixMatch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import utils.MuzikFinderConstants;

public final class RequestHelper {
	
	public static int cpt = 0;
	
	/** Prevent instantiation. */
	private RequestHelper(){ }
	
	public static String createRequest(String method, Map<String, String> params){
		String url = MuzikFinderConstants.API_URL;
		url+= method + "?";
		url+= MuzikFinderConstants.API_KEY_NAME+"="+MuzikFinderConstants.API_KEY_REGXP;
		
		for(String key : params.keySet()){
			url += "&" + key+ "=" + params.get(key);
		}
		
		return url;
	}
	
	public static String sendRequest(String requestString) {
		return sendRequest(requestString, 0);
	}
	
	public static String sendRequest(String requestString, int api_key_position) {
		cpt++; // TODO: to remove, just give the actual number of generated requests.
		StringBuffer buffer = new StringBuffer();
		BufferedReader in;
		URL url;
		String key;
		boolean responseOK = false;
		int position_copy = api_key_position;

		do{
			try {
				key = MuzikFinderConstants.API_KEYS[position_copy];
				requestString = requestString.replaceFirst(MuzikFinderConstants.API_KEY_REGXP, key);
				
				url = new URL(requestString);
				in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
				String str;
	
				while ((str = in.readLine()) != null) {
					buffer.append(str);
				}
				
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		
			if(buffer.toString().contains("\"status_code\":401")){
				System.out.println("Pu assez de crédit d'API pour la clé "+key+" pour aujourd'hui...");
				position_copy = (position_copy+1) % MuzikFinderConstants.API_KEYS.length; // permet de parcourir chaque clef
			} else {
				responseOK = true;
			}
		} while (!responseOK && position_copy!=api_key_position); // Soit responseOk, soit toutes les clefs ont été testées
			
		return responseOK ? buffer.toString() : null;
	}

}