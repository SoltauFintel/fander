package de.mwvb.fander.auth;

import java.util.Map;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.service.PersonenService;
import de.mwvb.maja.auth.Authorization;
import spark.Request;
import spark.Response;

public class SAuthorization implements Authorization {
	private final Map<String, String> users;

	public SAuthorization() {
		users = new PersonenService().getLogins();
	}

	@Override
	public String check(Request req, Response res, String name, String pw, String service) {
		if (pw.isEmpty()) {
			Logger.warn("Zugang nicht gestattet für '" + name + "', da Passwort leer.");
			throw new RuntimeException("Bitte Passwort eingeben!");
		}
		for (Map.Entry<String,String> e : users.entrySet()) {
			if (name.equalsIgnoreCase(e.getKey())) {
				if (e.getValue().isEmpty()) {
					// Passwort vergeben! Programmfehler!
					Logger.error("Programmfehler: Zugang nicht gestattet für '" + name + "', da der Benutzer kein Passwort hat.");
					throw new RuntimeException("Benutzer nicht bekannt. Bitte an Administrator wenden.");
				} else if (e.getValue().equals(pw)) {
					return null; // Login erfolgreich
				}
				Logger.warn("Zugang nicht gestattet für '" + name + "', da Passwort falsch.");
				throw new RuntimeException("Zugang nicht gestattet, da das Passwort falsch ist. Bitte gehe zurück und gib das Passwort erneut ein."
						+ " Wende dich bitte an den Administrator falls Du dein Passwort vergessen haben solltest.");
			}
		}
		Logger.warn("Zugang nicht gestattet für '" + name + "', da Benutzername falsch.");
		throw new RuntimeException("Zugang nicht gestattet, da der Benutzer nicht bekannt ist."
				+ " Bitte gehe zurück und kontrolliere die Schreibweise des Benutzernamens.");
	}

	@Override
	public boolean isRelevant(String service) {
		return "S".equals(service);
	}
	
	public String richtigeSchreibweise(String user) {
		for (String key : users.keySet()) {
			if (key.equalsIgnoreCase(user)) {
				return key;
			}
		}
		return user;
	}
}
