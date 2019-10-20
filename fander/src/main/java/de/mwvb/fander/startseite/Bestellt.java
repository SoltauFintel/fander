package de.mwvb.fander.startseite;

import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class Bestellt extends BestellungMoeglich {
	
	public Bestellt(Woche woche, String user, FanderService sv) {
		super(woche, user, sv);
	}
	
	@Override
	public boolean isShowBestellungAendern() {
		return true;
	}
}
