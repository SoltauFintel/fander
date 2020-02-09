package de.mwvb.fander.model;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

@Entity
@Indexes({
	@Index(fields = { @Field("user") }, options = @IndexOptions(unique = true)),
	@Index(fields = { @Field("login") }, options = @IndexOptions(unique = true))
})
public class User {
	@Id
	private String id;
	/** Wird für die Referenzierung in der DB benutzt. Wird außerdem für die Anzeige in der Anwendung benutzt. Ist gewöhnlich der Vorname. */
	private String user;
	/** User Kennung fürs Einloggen */
	private String login;
	private String kennwort; // TODO derzeit noch Klartext
	private String vorname;
	private String nachname;
	/** false: männlich oder unbekannt, true: weiblich */
	private boolean weiblich;
	/** Person möchte Infomail bekommen */
	private boolean infomail;
	/** Person bestellt regelmäßig */
	private boolean typischerBesteller;
	private boolean zusatzstoffeAnzeigen;
	private String emailadresse;
	private boolean aktiv = true;
	private String token;
	private String publicNote;
	private String publicNoteTimestamp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getKennwort() {
		return kennwort;
	}

	public void setKennwort(String kennwort) {
		this.kennwort = kennwort;
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
	
	public String getName() {
		return getVorname() + " " + getNachname();
	}

	public boolean isWeiblich() {
		return weiblich;
	}

	public void setWeiblich(boolean weiblich) {
		this.weiblich = weiblich;
	}

	public boolean isInfomail() {
		return infomail;
	}

	public void setInfomail(boolean ausgewaehlt) {
		this.infomail = ausgewaehlt;
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

	
	public void setEmailadresse(String emailadresse) {
		this.emailadresse = emailadresse;
	}

	public String getEmailadresse() {
		return emailadresse;
	}

    public boolean isAktiv() {
        return aktiv;
    }

    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPublicNote() {
        return publicNote;
    }

    public void setPublicNote(String publicNote) {
        this.publicNote = publicNote;
    }

    public String getPublicNoteTimestamp() {
        return publicNoteTimestamp;
    }

    public void setPublicNoteTimestamp(String publicNoteTimestamp) {
        this.publicNoteTimestamp = publicNoteTimestamp;
    }
}
