package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.service.PersonenService;

public class MigrateUsers extends SActionBase {

	@Override
	protected void execute() {
		info("MigrateUsers");
		new PersonenService().saveUsers();
		res.redirect("/");
	}
}
