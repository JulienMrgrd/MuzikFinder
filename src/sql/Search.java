package sql;

import java.sql.Date;

public class Search {

	private String id;
	private String recherche;
	private Date dateSearch;
	
	public Search(String id, String recherche, Date dateSearch){
		this.id=id;
		this.recherche=recherche;
		this.dateSearch=dateSearch;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRecherche() {
		return recherche;
	}

	public void setRecherche(String recherche) {
		this.recherche = recherche;
	}

	public Date getDateSearch() {
		return dateSearch;
	}

	public void setDateSearch(Date dateSearch) {
		this.dateSearch = dateSearch;
	}
	
}
