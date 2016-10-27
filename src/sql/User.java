package sql;

import java.sql.Date;

public class User {

	private String id;
	private String login;
	private String password;
	private String email;
	private Date dateBirth;
	
	public User(String id, String login, String password, String email, Date dateBirth){
		this.id=id;
		this.login=login;
		this.password=password;
		this.email=email;
		this.dateBirth=dateBirth;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDateBirth() {
		return dateBirth;
	}

	public void setDateBirth(Date dateBirth) {
		this.dateBirth = dateBirth;
	}
	
	public String toString(){
		return "id = "+this.id+" login = "+this.login+" password ="+this.password+" email = "+this.email+" date = "+this.dateBirth;
	}
}
