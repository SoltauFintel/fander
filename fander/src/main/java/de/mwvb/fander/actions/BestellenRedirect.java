package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

/**
 * Hier sieht der Mitarbeiter entweder seine Bestellung oder kann bestellen.
 * Falls es keine Woche gibt oder das System nicht für Fander konfiguriert ist, wird eine Fehlermeldung ausgegeben.
 */
public class BestellenRedirect extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		Woche woche = sv.getJuengsteWoche();
		if (woche == null) {
			throw new UserMessage("Bestellen nicht möglich!");
		}

		Mitarbeiterbestellung mb = sv.getMitarbeiterbestellung(woche, user());
		if (mb != null && !mb.getBestellungen().isEmpty()) {
			res.redirect("/" + woche.getStartdatum() + "/bestellt");
		} else {
			res.redirect("/" + woche.getStartdatum());
		}
	}
}
