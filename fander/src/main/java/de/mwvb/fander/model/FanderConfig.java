package de.mwvb.fander.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity
public class FanderConfig {
	public static final String ID = "1";
	@Id
	private String id = ID;
	/** Ansprechpartner */
	private String admin = "";
	private String url = "";
	private int mindestbestellmenge = 2;
	private double mindestbestellbetrag = 0d;
	/** true: Menü aus HTML-Datei. false: Menü aus WWW. */
	private boolean demomodus = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Ansprechpartner (User). Liefert "?" wenn noch nicht belegt.
	 */
	public String getAdmin() {
		return admin == null || admin.trim().isEmpty() ? "?" : admin;
	}

	/**
	 * @param admin User der aktueller Ansprechpartner ist
	 */
	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getMindestbestellmenge() {
		return mindestbestellmenge;
	}

	public void setMindestbestellmenge(int mindestbestellmenge) {
		this.mindestbestellmenge = mindestbestellmenge;
	}

	public double getMindestbestellbetrag() {
		return mindestbestellbetrag;
	}

	public void setMindestbestellbetrag(double mindestbestellbetrag) {
		this.mindestbestellbetrag = mindestbestellbetrag;
	}

	public boolean isDemomodus() {
		return demomodus;
	}

	public void setDemomodus(boolean demomodus) {
		this.demomodus = demomodus;
	}
}
