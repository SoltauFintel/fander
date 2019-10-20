package de.mwvb.fander.startseite;

import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class MoechteNichtBestellen extends BestellungMoeglich {

	public MoechteNichtBestellen(Woche woche, String user, FanderService sv) {
		super(woche, user, sv);
	}
	
	@Override
	public String getH1Title() {
		return "Ich möchte nichts bestellen.";
	}
	
	@Override
	public boolean isShowDochBestellen() {
		return true;
	}
	
	@Override
	public boolean isShowBestellsumme(double summe) {
		return false;
	}
}
