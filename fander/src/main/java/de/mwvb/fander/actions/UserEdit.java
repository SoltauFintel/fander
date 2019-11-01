package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.User;

/**
 * wenn id = UserService.CREATE: neuen Benutzer anlegen.
 * Sonst Benutzer bearbeiten.
 */
public class UserEdit extends SAction {

	@Override
	protected void execute() {
		if (!isUserManager()) {
			throw new AuthException();
		}
		String id = req.params("id");
		User user = new UserService().byId(id);
		if (user == null) {
			throw new UserMessage("User nicht vorhanden");
		}
		setTitle("Benutzer bearbeiten: " + user.getUser());
		
		put("id", id); // nicht user.getId() !
		put("user4edit", user.getUser());
		put("login", user.getLogin());
		put("vorname", user.getVorname());
		put("nachname", user.getNachname());
		put("emailadresse", user.getEmailadresse());
		put("weiblich", user.isWeiblich());
		put("infomail", user.isInfomail());
		put("typischerBesteller", user.isTypischerBesteller());
		put("zusatzstoffeAnzeigen", user.isZusatzstoffeAnzeigen());
		put("aktiv", user.isAktiv());
	}
	
	@Override
	public String getPage() {
		return "user";
	}
}
