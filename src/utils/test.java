package utils;

import java.net.URISyntaxException;
import java.sql.SQLException;

import sql.mysql.MySQLService;

public class test {
	
	public static void main(String[] args){
		
		try {
			MySQLService ms = new MySQLService();
			ms.insertNewUser("feligo", "password", "fff@yopmail");
			ms.insertNewUser("felix", "password2", "email");
			ms.insertNewUser("feligo", "password2", "newMail");
			System.out.println(ms.verifyConnexion("feligo", "password"));
			System.out.println(ms.verifyConnexion("feligo", "password2"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
