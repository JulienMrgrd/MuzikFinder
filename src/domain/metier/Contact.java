package domain.metier;

public class Contact {
	
	private long id_contact;
	private String firstName;
	private String lastName;
	private String email;
	
	public Contact() { }
	
	public Contact(long id_contact, String firstName, String lastName, String email) {
		super();
		this.id_contact = id_contact;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public Contact(Contact copy){
		this(copy.getId(), copy.getFirstName(), copy.getLastName(), copy.getEmail());
	}

	public long getId() {
		return id_contact;
	}

	public void setId(long id_contact) {
		this.id_contact = id_contact;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
