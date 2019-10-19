package de.mwvb.fander.actions;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Gerichtbestellung;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class BestellungAbsenden extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		String user = user();
		Woche woche = sv.byStartdatum(req);
		if (!woche.isBestellungenErlaubt()) {
			throw new UserMessage("Woche " + woche.getStartdatum() + " darf nicht mehr verändert werden!");
		}
		
		Mitarbeiterbestellung mb = sv.getMitarbeiterbestellung(woche, user);
		info(mb.getSpeicherinfo());
		setLimit(mb);
		sv.bestellteGerichteVerzeichnen(woche, mb, req);
		log(mb, woche, sv);
		sv.save(woche);
		int n = mb.getBestellungen().size();
		info("Mitarbeiter " + user + " hat " + n + " Gericht" + (n == 1 ? "" : "e") + " bestellt.");

		res.redirect("/" + woche.getStartdatum() + "/bestellt");
	}

	/** Bestellungen ausloggen, damit die im Falle eines Absturzes noch wenigstens in der Console bzw. im Log stehen. */
	private void log(Mitarbeiterbestellung mb, Woche woche, FanderService sv) {
		String a = "Bestellung für User " + mb.getUser() + " in Fander Woche " + woche.getStartdatum() + " abgesendet. Limit: "
				+ (mb.getLimit() == null ? "kein" : mb.getLimit()) + ", "
				+ mb.getBestellungen().size() + " Gericht(e):";
		for (Gerichtbestellung gb : mb.getBestellungen()) {
			Gericht gericht = sv.getGericht(gb.getGerichtId(), woche);
			a += "\n- " + gericht.getTitel();
		}
		Logger.info(a);
	}

	private void setLimit(Mitarbeiterbestellung mb) {
		try {
			int limit = Integer.parseInt(req.queryParams("limit"));
			mb.setLimit(limit);
		} catch (NumberFormatException e) {
			mb.setLimit(null);
		}
	}
}
