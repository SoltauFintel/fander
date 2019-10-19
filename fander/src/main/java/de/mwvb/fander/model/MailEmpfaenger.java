package de.mwvb.fander.model;

public class MailEmpfaenger {
	private String id;
	/** User.user, gewöhnlich der Vorname */
	private String name;
	/** Infomail erhalten? */
	private boolean ausgewaehlt = false;
	/** immer gültige Emailadresse */
	private String emailadresse;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAusgewaehlt() {
		return ausgewaehlt;
	}

	public void setAusgewaehlt(boolean ausgewaehlt) {
		this.ausgewaehlt = ausgewaehlt;
	}

	public String getEmailadresse() {
		return emailadresse;
	}

	public void setEmailadresse(String emailadresse) {
		this.emailadresse = emailadresse;
	}
}
