package tests;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import nosql.mongo.MongoService;
import nosql.mongo.MongoServiceInsert;
import nosql.mongo.MongoServiceSearchUser;
import server.services.MuzikFinderService;
import sql.metier.User;
import sql.mysql.MySQLService;

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
		
		MuzikFinderService mzf = new MuzikFinderService();
		

		/*MongoService ms = MongoService.getInstance();
		List<String> tags= new ArrayList<>();
		tags.add("i");
		ms.searchMusics(tags, "59563259"); // pour que la connexion a mongo soit bien faite une premiere fois
		System.out.println("//////////////////////FIN PRECHAUFFE MONGO////////////////");
		MuzikFinderService mzf = new MuzikFinderService();
		User us=mzf.checkConnection("feligo", "password");
		
		tags= new ArrayList<>();
		tags.add("let");
		tags.add("me");
		tags.add("love");
//		for(MusicDTO mdto: mzf.searchMusics(tags, "feligo596326") ){
//			//ms.addNewSearch("114588667", us.getDateBirth());
//		}
//		ms.addNewSearch("83747620", us.getDateBirth());
		tags.clear();
		
		Instant start = Instant.now();
		
		Instant end = Instant.now();
		System.out.println("Temps reel de la recherche =="+Duration.between(start, end));
	*/
	}

}
