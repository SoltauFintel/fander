package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.service.FanderService;

/**
 * Nachdem der Ansprechpartner bei Fander angerufen hat, setzt sie/er den Bestellstatus auf 'bestellt'.
 */
public class FanderBestellstatusAction extends SActionBase {

	@Override
	protected void execute() {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}
		
		boolean bestellt = "1".equals(req.queryParams("s")); // 0: nicht bestellt, 1: angerufen
		new FanderService().angerufen(req, bestellt);
		
		res.redirect("/anruf");
	}
}
