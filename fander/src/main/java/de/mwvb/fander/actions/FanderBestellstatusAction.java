package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

/**
 * Nachdem der Fander-Admin bei Fander angerufen hat setzt sie/er den Bestellstatus auf bestellt.
 */
public class FanderBestellstatusAction extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		if (!sv.getConfig().getAdmin().equalsIgnoreCase(user())) {
			throw new AuthException();
		}

		Woche woche = sv.byStartdatum(req);
		if ("1".equals(req.queryParams("s"))) { // Status auf "bestellt" setzen!
			woche.setBestellt(true);
			sv.save(woche);
		} else { // Status auf "noch nicht bestellt" setzen!
			woche.setBestellt(false);
			sv.save(woche);
		}
		res.redirect("/" + woche.getStartdatum() + "/anruf");
	}
}
