package domain.metier;

import java.util.Set;

public class Contact {
	
	private long id_contact;
	private String firstName;
	private String lastName;
	private String email;
	private Address add;
	private Set<ContactGroup> books;
	private Set<PhoneNumber> phones;
	
	public Contact() { }
	
	public Contact(long id_contact, String firstName, String lastName, String email) {
		super();
		this.id_contact = id_contact;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	public Contact(long id_contact, String firstName, String lastName, String email, Address add, Set<ContactGroup> books,
						Set<PhoneNumber> phones){
		super();
		this.id_contact = id_contact;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.add = add;
		this.books = books;
		this.phones = phones;
	}
	
	public Contact(Contact copy){
		this(copy.getId(), copy.getFirstName(), copy.getLastName(), copy.getEmail(), 
				copy.getAdd(), copy.getBooks(), copy.getProfiles());
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

	public Address getAdd() {
		return add;
	}

	public void setAdd(Address add) {
		this.add = add;
	}

	public Set<ContactGroup> getBooks() {
		return books;
	}

	public void setBooks(Set<ContactGroup> books) {
		this.books = books;
	}

	public Set<PhoneNumber> getProfiles() {
		return phones;
	}

	public void setPhones(Set<PhoneNumber> phones) {
		this.phones = phones;
	}
	
	
}
