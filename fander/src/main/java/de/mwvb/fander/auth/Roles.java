package de.mwvb.fander.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.service.FanderService;
import de.mwvb.maja.web.AppConfig;

public class Roles {
	private final AppConfig appConfig = new AppConfig();
	private final FanderConfig config = new FanderService().getConfig();
	
	public String getRollen(String user) {
		List<String> rollen = new ArrayList<>();
		if (isUserManager(user)) {
			rollen.add("User-Manager");
		}
		if (isDeveloper(user)) {
			rollen.add("Developer");
		}
		if (isAnsprechpartner(user)) {
			rollen.add("Ansprechpartner");
		}
		return rollen.stream().collect(Collectors.joining(", "));
	}

	/**
	 * @param user i.d.R. akt. User
	 * @return true: darf Woche starten und Bestellung schließen
	 */
	public boolean isAnsprechpartner(String user) {
		return user.equalsIgnoreCase(config.getAdmin());
	}

	/**
	 * @param user i.d.R. akt. User
	 * @return true: darf Benutzer verwalten und Einstellungen vornehmen
	 */
	public boolean isUserManager(String user) {
		return isRole(user, "user-manager");
	}
	
	/**
	 * @param user i.d.R. akt. User
	 * @return true: darf alle Developer-Funktionen ausführen, z.B. Anmelden als
	 */
	public boolean isDeveloper(String user) {
		return isRole(user, "developer");
	}

	private boolean isRole(final String user, String configKey) {
		final String users = appConfig.get(configKey);
		if (users != null) {
			for (String w : users.split(",")) {
				if (w.trim().equalsIgnoreCase(user)) {
					return true;
				}
			}
		}
		return false;
	}
}
