package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;

public class NeueWocheForce extends SAction {

	@Override
	protected void execute() {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}

		String startdatum = req.params("startdatum");
		put("startdatum", startdatum);
	}
}
