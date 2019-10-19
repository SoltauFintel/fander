package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class UnsereKarteRedirect extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		Woche woche = sv.getJuengsteWoche();
		if (woche == null) {
			throw new RuntimeException("Unsere Karte ist aktuell nicht verfügbar.");
		}

		res.redirect("/" + woche.getStartdatum() + "/unsere-karte");
	}
}
