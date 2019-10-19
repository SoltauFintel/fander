package de.mwvb.fander.model;

public class KPerson {
	private String user; // muss eindeutig sein, gewöhnlich der Vorname
	private String vorname;
	private String nachname;
	private String kennwort; // TODO derzeit noch Klartext
	private boolean weiblich;
	/** Person möchte Infomail bekommen */
	private boolean ausgewaehlt; // TODO rename to: vorschlagen -oder- infomail
	/** Person bestellt regelmäßig */
	private boolean typischerBesteller;
	private boolean zusatzstoffeAnzeigen;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getVorname() {
		return vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public String getNachname() {
		return nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getKennwort() {
		return kennwort;
	}

	public void setKennwort(String kennwort) {
		this.kennwort = kennwort;
	}

	public boolean isWeiblich() {
		return weiblich;
	}

	public void setWeiblich(boolean weiblich) {
		this.weiblich = weiblich;
	}

	public boolean isAusgewaehlt() {
		return ausgewaehlt;
	}

	public void setAusgewaehlt(boolean ausgewaehlt) {
		this.ausgewaehlt = ausgewaehlt;
	}

	public boolean isTypischerBesteller() {
		return typischerBesteller;
	}

	public void setTypischerBesteller(boolean typischerBesteller) {
		this.typischerBesteller = typischerBesteller;
	}

	public boolean isZusatzstoffeAnzeigen() {
		return zusatzstoffeAnzeigen;
	}

	public void setZusatzstoffeAnzeigen(boolean zusatzstoffeAnzeigen) {
		this.zusatzstoffeAnzeigen = zusatzstoffeAnzeigen;
	}
	
	public String getEmailadresse() {
		return (getVorname() + "." + getNachname()).toLowerCase().replace("ä", "ae").replace("ö", "oe").replace("ü", "ue");
	}
	
	public String getName() {
		return getVorname() + " " + getNachname();
	}
}
