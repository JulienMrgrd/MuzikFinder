package sql;

import java.net.URISyntaxException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import sql.metier.Search;
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
			mySqlService = new MySQLService();
		} catch (ExceptionInInitializerError | ClassNotFoundException | URISyntaxException | SQLException error) {
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
	
	public void addSearch(String id_user, String recherche, String id_recherche) {
		mySqlService.addSearch(id_user, recherche, id_recherche);
	}
	
	public void update(String id_user, String newPassword, String newEmail){
		mySqlService.update(id_user, newPassword, newEmail);
	}
	
	public void deleteSearchByDateAndUser(String id_user, Date date) {
		mySqlService.deleteSearchByDateAndUser(id_user, date);
	}
	
	public List<Search> getSearchByDateAndUser(String id_user, Date date) {
		return mySqlService.getSearchByDateAndUser(id_user, date);
	}

	public void deleteAccountUser(String id_user) {
		mySqlService.deleteAccountUser(id_user);
	}
		
	public void deleteSearchUser(String id_user){
		mySqlService.deleteSearchUser(id_user);
	}
}
