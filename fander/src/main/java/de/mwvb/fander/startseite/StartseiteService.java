package de.mwvb.fander.startseite;

import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;
import de.mwvb.fander.service.KeineWocheException;

public class StartseiteService {

	public Zustand getZustand(String user, boolean bestellungAendern) {
		try {
			FanderService sv = new FanderService();
			Woche woche = sv.getJuengsteWoche();
			Zustand zustand = getBestellungOffenZustand(woche, user, sv, bestellungAendern);
			if (!woche.isBestellungenErlaubt()) {
				return new BestellungGeschlossen(zustand);
			}
			return zustand;
		} catch (KeineWocheException e) {
			return new KeineWocheZustand(user);
		}
	}
	
	private Zustand getBestellungOffenZustand(Woche woche, String user, FanderService sv, boolean bestellungAendern) {
		boolean nichtBestellen = woche.getNichtBestellen().contains(user);
		if (nichtBestellen) {
			return new MoechteNichtBestellen(woche, user, sv);
		}
		Mitarbeiterbestellung mb = sv.getMitarbeiterbestellung(woche, user);
		if (mb == null || mb.getBestellungen().isEmpty()) { // User hat nicht bestellt.
			return new NochNichtBestellt(woche, user, sv);
		} else {
			sv.bestellteGerichteVerzeichnen(woche, mb);
			if (bestellungAendern) {
				return new NochNichtBestellt(woche, user, sv);
			} else {
				return new Bestellt(woche, user, sv);
			}
		}
	}
}
