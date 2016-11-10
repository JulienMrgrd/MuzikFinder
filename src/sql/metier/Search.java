package sql.metier;

import java.sql.Date;

public class Search {

	private String id;
	private String recherche;
	private String idRecherche;
	private Date dateSearch;
	
	public Search(String id, String recherche, String idRecherche, Date dateSearch){
		this.id=id;
		this.recherche=recherche;
		this.idRecherche=idRecherche;
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

	public String getIdRecherche() {
		return idRecherche;
	}

	public void setIdRecherche(String idRecherche) {
		this.idRecherche = idRecherche;
	}

	public Date getDateSearch() {
		return dateSearch;
	}

	public void setDateSearch(Date dateSearch) {
		this.dateSearch = dateSearch;
	}
	
}
