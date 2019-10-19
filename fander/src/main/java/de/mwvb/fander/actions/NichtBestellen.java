package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.service.FanderService;

public class NichtBestellen extends SActionBase {

	@Override
	protected void execute() {
		new FanderService().nichtBestellen(req);
		res.redirect("/");
	}
}
