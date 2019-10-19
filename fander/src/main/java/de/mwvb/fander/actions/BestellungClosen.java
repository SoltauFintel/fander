package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class BestellungClosen extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		if (!sv.getConfig().getAdmin().equalsIgnoreCase(user())) {
			throw new AuthException();
		}

		Woche woche = sv.byStartdatum(req);
		
		if ("1".equals(req.queryParams("open"))) {
			open(sv, woche);
		} else {
			close(sv, woche);
		}
	}

	private void close(FanderService sv, Woche woche) {
		if (!woche.isBestellungenErlaubt()) {
			throw new UserMessage("Bestellung ist doch schon geschlossen!");
		}
		
		woche.setBestellungenErlaubt(false);
		woche.setBestellt(false);
		sv.save(woche);
		res.redirect("/" + woche.getStartdatum() + "/anruf");
	}

	private void open(FanderService sv, Woche woche) {
		if (woche.isBestellungenErlaubt()) {
			throw new UserMessage("Bestellung ist doch schon offen!");
		}
		
		woche.setBestellungenErlaubt(true);
		sv.save(woche);
		res.redirect("/");
	}
}
