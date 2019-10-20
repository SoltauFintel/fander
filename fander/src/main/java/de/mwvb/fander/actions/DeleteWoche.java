package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.service.FanderService;

public class DeleteWoche extends SActionBase {

	@Override
	protected void execute() {
		if (!isDeveloper()) {
			throw new AuthException();
		}

		String startdatum = req.queryParams("startdatum");
		if (new FanderService().delete(startdatum)) {
			info("Woche " + startdatum + " gelöscht");
		}

		res.redirect("/wochen");
	}
}
