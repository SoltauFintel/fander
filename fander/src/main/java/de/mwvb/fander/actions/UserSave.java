package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.UserMessage;

public class UserSave extends AbstractSaveUser {

	@Override
	protected void execute() {
		if (!isUserManager()) {
			throw new AuthException();
		}

		String login = req.queryParams("login");
		checkLogin(login);
		String anzeigename = req.queryParams("user4edit");
		if (anzeigename == null || anzeigename.trim().length() < 2 || !anzeigename.trim().equals(anzeigename)) {
			throw new UserMessage("Bitte User eingeben!");
		}
		
		String id = req.params("id");
		user = sv.byId(id);
		if (user == null) {
			throw new UserMessage("User nicht vorhanden!");
		}
		
		changePassword(false);

		save(login);

		user.setUser(anzeigename);
		user.setVorname(req.queryParams("vorname").trim());
		user.setNachname(req.queryParams("nachname").trim());
		user.setWeiblich(on("weiblich"));
		user.setAktiv(on("aktiv"));

		sv.save(user);
		if (UserService.CREATE.equals(id)) {
			info("saved new User: " + user.getUser() + "  #" + user.getId());
		} else {
			info("saved User: " + user.getUser() + "  #" + user.getId());
		}
		res.redirect("/users");
	}
}
