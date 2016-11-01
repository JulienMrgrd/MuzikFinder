package tests;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nosql.mongo.MongoService;
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
		
		List<String> tags= new ArrayList<>();
		tags.add("everybody");
		tags.add("green");
		MongoService ms =new MongoService(false);
		ms.searchMusics(tags);
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
		/*
			
		
		System.out.println(MathUtils.calculAge(datBirth));
		System.out.println("1semaine === "+(datNow.getTime()-datNowMoin1Semaine.getTime()));
		
		try {
			ms = new MySQLService();
			generate10User();
			//ms.seeAllDBUser();
			
			User user = ms.verifyConnexion("EL PADRE", "BIG BOSS");
			System.out.println(MathUtils.calculAge(user.getDateBirth()));
			System.out.println(user);
			user = ms.deleteAccountUser(user);
			System.out.println(user);
			//ms.seeAllDBUser();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
