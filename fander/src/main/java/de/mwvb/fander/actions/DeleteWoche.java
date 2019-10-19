package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class DeleteWoche extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		if (!sv.getConfig().getAdmin().equalsIgnoreCase(user())) {
			throw new AuthException();
		}

		Woche woche = sv.byStartdatum(req);
		sv.delete(woche);
		info("Woche " + woche.getStartdatum() + " gelöscht");

		res.redirect("/wochen");
	}
}
