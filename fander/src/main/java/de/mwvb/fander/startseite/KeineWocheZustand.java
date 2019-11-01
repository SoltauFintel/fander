package de.mwvb.fander.startseite;

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
}
