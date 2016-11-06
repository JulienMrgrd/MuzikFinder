package tests;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import nosql.mongo.MongoService;
import server.dto.MusicDTO;
import sql.mysql.MySQLService;
import utils.TimeInMilliSeconds;

public class Test {
	static MySQLService ms;
	
	public static void generate10User() throws SQLException, URISyntaxException{

		ms.createNewUser("feligo", "password", "fff@yopmail",2016-1900,5,8);
		ms.createNewUser("felix", "password2", "email",2016-1900,5,8);
		ms.createNewUser("feligo2", "DAR", "newMail",2016-1900,5,8);
		
		ms.createNewUser("mouned", "password", "mouned@yopmail",2016-1900,5,8);
		ms.createNewUser("moussa", "password2", "email",2016-1900,5,8);
		ms.createNewUser("mouned2", "DAR", "newMail",2016-1900,5,8);
		
		ms.createNewUser("juju", "password", "mouned@yopmail",2016-1900,5,8);
		ms.createNewUser("julien", "password2", "email",2016-1900,5,8);
		ms.createNewUser("julien2", "DAR", "newMail",2016-1900,5,8);
		
		ms.createNewUser("EL PADRE", "BIG BOSS", "DEMANGEAON@yopmail",1994-1900,12,8);
	}
	
	public static void main(String[] args){

		MongoService ms =new MongoService(false);
		List<String> tags= new ArrayList<>();
		tags.add("i");
		ms.searchMusics(tags, "59563259"); // pour que la connexion a mongo soit bien faite une premiere fois
		tags.clear();
		tags= new ArrayList<>();
		tags.add("am");
		tags.add("i");
		/*tags.add("out");
		tags.add("of");
		tags.add("my");
		tags.add("head");*/
		Instant start = Instant.now();
		ms.searchMusics(tags, "59563259");
		Instant end = Instant.now();
		System.out.println("Temps reel de la recherche =="+Duration.between(start, end));
		
		/*tags.removeAll(tags);
		System.out.println(tags.size());
		tags.add("les");
		tags.add("vainqueurs");
		tags.add("l'écrivent");
		start = Instant.now();
		ms.searchMusics(tags, "mplpdekeo");
			//System.out.println(msDTO.getTrackName());
			//System.out.println(msDTO.getArtistId());
		
		end = Instant.now();
		System.out.println("Duree 2eme recherche =="+Duration.between(start, end));*/
		//YYYY-MM-DD
		/*String dateBirth = "1994-12-05";
		java.sql.Date datBirth = java.sql.Date.valueOf(dateBirth);
		String dateMoin1Semain = "2016-09-27";
		java.sql.Date datNowMoin1Semaine = java.sql.Date.valueOf(dateMoin1Semain);
		String dateNow = "2016-10-27";
		java.sql.Date datNow = java.sql.Date.valueOf(dateNow);
		
		
		java.util.Date utilDate = new java.util.Date();
	    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
	    long mmm=utilDate.getTime()-datNowMoin1Semaine.getTime();
		System.out.println(TimeInMilliSeconds.MONTH);
		System.out.println("mmmm = "+mmm);*/
		
	}

}
