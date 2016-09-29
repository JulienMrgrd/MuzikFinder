package domain.metier;

import java.util.Set;

public class ContactGroup {
	
	private long id_group;
	private String groupName;
	private Set<Contact> contacts;
	
	public ContactGroup() { }
	
	public ContactGroup(long id_group, String groupName,Set<Contact> contacts) {
		super();
		this.id_group = id_group;
		this.groupName = groupName;
		this.setContacts(contacts);
	}

	public long getId() {
		return id_group;
	}

	public void setId(long id_group) {
		this.id_group = id_group;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}
	

}
