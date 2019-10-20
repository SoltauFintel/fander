package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.UserMessage;

public class UserSave extends AbstractSaveUser {

	@Override
	protected void execute() {
		String login = req.queryParams("login");
		checkLogin(login);
		String anzeigename = req.queryParams("user");
		if (anzeigename == null || anzeigename.trim().length() < 2 || !anzeigename.trim().equals(anzeigename)) {
			throw new UserMessage("Bitte User eingeben!");
		}

		if (!isUserManager()) {
			throw new AuthException();
		}
		
		user = sv.byId(req.params("id"));
		if (user == null) {
			throw new UserMessage("User nicht vorhanden!");
		}
		
		changePassword(false);

		save(login);

		user.setUser(anzeigename);
		user.setVorname(req.queryParams("vorname").trim());
		user.setNachname(req.queryParams("nachname").trim());
		user.setWeiblich(on("weiblich"));

		sv.save(user);
		info("save User: " + user.getUser());
		res.redirect("/users");
	}
}
