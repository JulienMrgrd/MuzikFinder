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
		url+= MuzikFinderConstants.API_KEY+"="+MuzikFinderConstants.API_KEY;
		
		for(String key : params.keySet()){
			url += "&" + key+ "=" + params.get(key);
		}
		
		return url;
	}
	
	public static String sendRequest(String requestString) {
		cpt++;
		StringBuffer buffer = new StringBuffer();

		try {
			URL url = new URL(requestString);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream(), "UTF-8"));
			String str;

			while ((str = in.readLine()) != null) {
				buffer.append(str);
			}

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}

}