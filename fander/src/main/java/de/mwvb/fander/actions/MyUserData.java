package de.mwvb.fander.actions;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.User;

public class MyUserData extends SAction {

	@Override
	protected void execute() {
		info("MyUserData");
		User user = new UserService().getUser(user());
		setTitle("Benutzereinstellungen " + user.getLogin());
		put("anzeigename", user.getUser());
		put("login", user.getLogin());
		put("vorname", user.getVorname());
		put("nachname", user.getNachname());
		put("emailadresse", user.getEmailadresse());
		put("infomail", user.isInfomail());
		put("typischerBesteller", user.isTypischerBesteller());
		put("zusatzstoffeAnzeigen", user.isZusatzstoffeAnzeigen());
		put("weiblich", user.isWeiblich());
	}
}
