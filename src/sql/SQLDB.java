package sql;

import java.sql.Date;
import java.sql.SQLException;

import sql.metier.User;
import sql.mysql.MySQLService;

/**
 * Appel les fonctions de la BD choisies (MySQL, Oracle, etc ...)
 * @author JulienM
 */
public class SQLDB {

	protected MySQLService mySqlService;
	
	public SQLDB() {
		try {
			mySqlService = MySQLService.getInstance();
		} catch (ExceptionInInitializerError error) {
			error.printStackTrace();
		}
	}
	
	public User createNewUser(String pseudo, String password, String email, int year, int month, int day){
		return mySqlService.createNewUser(pseudo, password, email, year, month, day);
	}
	
	public User checkConnexion(String pseudo, String password) {
		return mySqlService.checkConnexion(pseudo, password);
	}
	
	public boolean checkLogin(String username) {
		return mySqlService.checkLogin(username);
	}
	
	public void addSearch(User user, String recherche) {
		mySqlService.addSearch(user, recherche);
	}
	
	public void setPassword(User user, String newPassword) {
		mySqlService.setPassword(user, newPassword);
	}
	
	public void setEmail(User user, String newEmail){
		mySqlService.setEmail(user, newEmail);
	}

	public void deleteSearchByDateAndUser(User user, Date date) {
		mySqlService.deleteSearchByDateAndUser(user, date);
	}
	
	public String getSearchByDateAndUser(User user, Date date) {
		return mySqlService.getSearchByDateAndUser(user, date);
	}

	public User deleteAccountUser(User user) {
		return mySqlService.deleteAccountUser(user);
	}
		
	public void deleteSearchUser(User user){
		mySqlService.deleteSearchUser(user);
	}
	
	//TODO: A supprimer
	////////////////METHODE UTILES JUSTE PENDANT LA PERIODE DE DEV/////////////////////
	public void seeAllDBUser() throws SQLException{
		mySqlService.seeAllDBUser();
	}
	
	public void seeAllDBSearch() throws SQLException{
		mySqlService.seeAllDBSearch();
	}

}
