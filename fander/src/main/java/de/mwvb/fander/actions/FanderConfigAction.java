package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.service.FanderService;

public class FanderConfigAction extends SAction {

	@Override
	protected void execute() {
		if (!isUserManager()) {
			throw new AuthException();
		}
		
		setTitle("Anwendungseinstellungen");
		FanderConfig config = new FanderService().getConfig();

		put("admin", config.getAdmin()); // Ansprechpartner
		put("url", config.getUrl());
		put("mindestbestellbetrag", FanderService.format(config.getMindestbestellbetrag()));
		put("mindestbestellmenge", "" + config.getMindestbestellmenge());
		put("demomodus", config.isDemomodus() ? "1" : "0");
		put("iban", esc(config.getIban()));
	}
	
	@Override
	public String getPage() {
		return "config";
	}
}
