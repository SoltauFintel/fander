package de.mwvb.fander.actions;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.User;

public abstract class AbstractSaveUser extends SActionBase {
	protected final UserService sv = new UserService();
	protected User user;

	protected void checkLogin(String login) {
		if (login == null || login.trim().length() < 2 || !login.trim().equals(login)) {
			throw new UserMessage("Bitte Login eingeben!");
		}
	}

	protected void changePassword(boolean pruefeAlt) {
		String neu = req.queryParams("pw_neu");
		String neu2 = req.queryParams("pw_neu2");
		if (neu.isEmpty()) { // Das 1. neue Kennwort ist der Trigger für die Kenntwortänderung.
			return;
		}
		if (pruefeAlt) {
			String alt = req.queryParams("pw_alt");
			if (alt.isEmpty()) {
				throw new UserMessage("Bitte altes Kennwort eingeben!");
			}
			alt = User.hash(alt, ".v0");
			if (!user.getKennwort().equals(alt)) {
				throw new UserMessage("Altes Kennwort ist falsch!");
			}
		}
		if (neu.length() < 3) {
			throw new UserMessage("Bitte gebe ein Kennwort ein, dass mindestens 3 Zeichen lang ist!");
		}
		if (neu.equals(neu2)) {
			// Neues Kennwort setzen
			user.setKennwort(User.hash(neu, ".v0"));
			info(getClass().getSimpleName() + ": new password set");
		} else {
			throw new UserMessage("Die beiden neuen Kennwörter stimmen nicht überein!");
		}
	}
	
	protected void save(String login) {
		user.setLogin(login);
		String ea = req.queryParams("emailadresse").trim();
		if (!ea.isEmpty() && !ea.contains("@")) {
			throw new UserMessage("Bitte gültige Emailadresse eingeben!");
		}
		user.setEmailadresse(ea);
		user.setInfomail(on("infomail"));
		user.setTypischerBesteller(on("typischerBesteller"));
		user.setZusatzstoffeAnzeigen(on("zusatzstoffeAnzeigen"));
	}
	
	protected boolean on(String key) {
		return "1".equals(req.queryParams(key));
	}
}
