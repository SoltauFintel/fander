package de.mwvb.fander.actions;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.User;

public class MyUserDataSave extends SActionBase {

	@Override
	protected void execute() {
		String login = req.queryParams("login");
		if (login == null || login.trim().length() < 2 || !login.trim().equals(login)) {
			throw new UserMessage("Bitte Login eingeben!");
		}
		
		UserService sv = new UserService();
		User user = sv.getUser(user());
		
		changePassword(user);
		
		user.setLogin(login.trim());
		user.setEmailadresse(req.queryParams("emailadresse").trim());
		user.setInfomail(on("infomail"));
		user.setTypischerBesteller(on("typischerBesteller"));
		user.setZusatzstoffeAnzeigen(on("zusatzstoffeAnzeigen"));
		
		sv.save(user);
		info("MyUserDataSave: user saved");
		res.redirect("/");
	}
	
	private boolean on(String key) {
		return "1".equals(req.queryParams(key));
	}

	private void changePassword(User user) {
		String alt = req.queryParams("pw_alt");
		String neu = req.queryParams("pw_neu");
		String neu2 = req.queryParams("pw_neu2");
		if (neu.isEmpty()) { // Das 1. neue Kennwort ist der Trigger für die Kenntwortänderung.
			return;
		}
		if (alt.isEmpty()) {
			throw new UserMessage("Bitte altes Kennwort eingeben!");
		}
		alt = User.hash(alt);
		if (!user.getKennwort().equals(alt)) {
			throw new UserMessage("Altes Kennwort ist falsch!");
		}
		if (neu.length() < 3) {
			throw new UserMessage("Bitte gebe ein Kennwort ein, dass mindestens 3 Zeichen lang ist!");
		}
		if (neu.equals(neu2)) {
			// Neues Kennwort setzen
			user.setKennwort(User.hash(neu));
			info("MyUserDataSave: new password set");
		} else {
			throw new UserMessage("Die beiden neuen Kennwörter stimmen nicht überein!");
		}
	}
}
