package de.mwvb.fander.startseite;

import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

/**
 * Noch nicht bestellt (bzw. keine Haken gesetzt) oder Bestellung ändern
 */
public class NochNichtBestellt extends BestellungMoeglich {
	
	public NochNichtBestellt(Woche woche, String user, FanderService sv) {
		super(woche, user, sv);
	}

	@Override
	public boolean isBestellmodus() {
		return true;
	}
}
