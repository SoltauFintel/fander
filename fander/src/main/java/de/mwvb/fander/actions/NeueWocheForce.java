package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.service.FanderService;

public class NeueWocheForce extends SAction {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		if (!sv.getConfig().getAdmin().equalsIgnoreCase(user())) {
			throw new AuthException();
		}

		String startdatum = req.params("startdatum");
		put("startdatum", startdatum);
	}
}
