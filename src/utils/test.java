package utils;

import java.net.URISyntaxException;
import java.sql.SQLException;

import sql.User;
import sql.mysql.MySQLService;

public class test {
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
		
		ms.createNewUser("EL PADRE", "BIG BOSS", "DEMANGEAON@yopmail",2016-1900,5,8);
	}
	
	public static void main(String[] args){
		//YYYY-MM-DD
		String dateBirth = "1994-12-05";
		java.sql.Date datBirth = java.sql.Date.valueOf(dateBirth);
		String dateNow = "2016-10-27";
		java.sql.Date datNow = java.sql.Date.valueOf(dateNow);
		System.out.println(MathUtils.calculAge(datBirth, datNow));
		
		try {
			ms = new MySQLService();
			generate10User();
			ms.seeAllDBUser();
			
			User user = ms.verifyConnexion("EL PADRE", "BIG BOSS");
			System.out.println(user);
			user = ms.deleteAccountUser(user);
			System.out.println(user);
			ms.seeAllDBUser();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
