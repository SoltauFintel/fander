package de.mwvb.fander.startseite;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.rest.TagJSON;
import de.mwvb.fander.rest.UnsereKarteJSON;
import de.mwvb.fander.service.FanderService;

public abstract class Zustand {
	private final Woche woche;
	private final String user;
	
	public Zustand(Woche woche, String user) {
		this.woche = woche;
		this.user = user;
	}

	public final Woche getWoche() {
		return woche;
	}

	public final boolean isWocheVorhanden() {
		return woche != null;
	}
	
	public String getUser() {
		return user;
	}

	public String getLimit() {
		if (woche.getBestellungen() != null) {
			for (Mitarbeiterbestellung mb : woche.getBestellungen()) {
				if (mb.getUser().equalsIgnoreCase(user)) {
					if (mb.getLimit() != null) {
						return "" + mb.getLimit();
					}
					break;
				}
			}
		}
		return "";
	}
	
	public boolean isZusatzstoffeAnzeigen() {
		return new UserService().getUser(user).isZusatzstoffeAnzeigen();
	}

	public boolean isBestellmodus() {
		return false;
	}
	
	public boolean isShowBestellungAendern() {
		return false;
	}
	
	public boolean isShowDochBestellen() {
		return false;
	}

	public String getH1Title() {
		return "Meine Bestellung";
	}

	public boolean isShowBestellsumme(double summe) {
		return true;
	}
	
	public FanderBestellung getBestellungVorschau() {
		return null;
	}

	public boolean showTag(Tag tag) {
		return isShowAlwaysTag() || !tag.getGerichte().isEmpty();
	}
	
	protected boolean isShowAlwaysTag() {
		return true;
	}

	public String getAnsprechpartner() {
		return new FanderService().getConfig().getAdmin();
	}
	
	public boolean isAnsprechpartnerWeiblich() {
		try {
			return new UserService().getUser(getAnsprechpartner()).isWeiblich();
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isNeueWocheHighlighted() {
	    return false;
	}
	
	public UnsereKarteJSON getJSON() {
	    UnsereKarteJSON k = new UnsereKarteJSON();
	    k.setStartdatum(getWoche().getStartdatum());
	    k.setAnsprechpartner(getAnsprechpartner());
	    k.setGeschlossen(!getWoche().isBestellungenErlaubt());
	    k.setUser(user);
	    k.setLimit(getLimit());
	    for (Tag tag : getWoche().getTage()) {
	        if (tag.getGerichte() != null && !tag.getGerichte().isEmpty()) {
    	        TagJSON n = new TagJSON();
    	        n.setTag(tag.getWochentagText());
    	        n.setTagNummer(tag.getWochentag());
    	        n.getGerichte().addAll(tag.getGerichte());
    	        k.getTage().add(n);
	        }
        }
	    return k;
	}
}
