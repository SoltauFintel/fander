package de.mwvb.fander.startseite;

import de.mwvb.fander.rest.UnsereKarteJSON;

public class KeineWocheZustand extends Zustand {

	public KeineWocheZustand(String user) {
		super(null, user);
	}
	
	@Override
	public String getH1Title() {
		return "Keine Woche vorhanden";
	}
	
	@Override
	public boolean isNeueWocheHighlighted() {
	    return true;
	}
	
	@Override
	public UnsereKarteJSON getJSON() {
	    return null;
	}
}
