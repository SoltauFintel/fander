package de.mwvb.fander.model;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.Transient;

//Embedded
public class Mitarbeiterbestellung {
	private String user;
	private final List<Gerichtbestellung> bestellungen = new ArrayList<>();
	private Integer limit = null;
	private String erstelltAm = "";   // kann in Altdaten leer sein
	private String bearbeitetAm = ""; // kann leer sein
	@Transient
	private String speicherinfo;

	public Mitarbeiterbestellung() {
	}

	public Mitarbeiterbestellung(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Gerichtbestellung> getBestellungen() {
		return bestellungen;
	}
	
	public void add(Gerichtbestellung b) {
		bestellungen.add(b);
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getErstelltAm() {
		return erstelltAm;
	}

	public void setErstelltAm(String erstelltAm) {
		this.erstelltAm = erstelltAm;
	}

	public String getBearbeitetAm() {
		return bearbeitetAm;
	}

	public void setBearbeitetAm(String bearbeitetAm) {
		this.bearbeitetAm = bearbeitetAm;
	}

	public String getSpeicherinfo() {
		return speicherinfo;
	}

	public void setSpeicherinfo(String speicherinfo) {
		this.speicherinfo = speicherinfo;
	}
}
