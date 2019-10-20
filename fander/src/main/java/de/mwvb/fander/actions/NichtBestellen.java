package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.service.FanderService;

public class NichtBestellen extends SActionBase {

	@Override
	protected void execute() {
		boolean undo = "1".equals(req.queryParams("undo"));

		new FanderService().nichtBestellen(req, user(), undo);
		if (undo) {
			info("Diese Woche doch bei Fander bestellen.");
		} else {
			info("Diese Woche nicht bei Fander bestellen.");
		}
		
		res.redirect("/");
	}
}
