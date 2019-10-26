package de.mwvb.fander.auth;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.model.User;
import de.mwvb.maja.auth.Authorization;
import spark.Request;
import spark.Response;

public class SAuthorization implements Authorization {

	@Override
	public String check(Request req, Response res, String login, String password, String service) {
		if (password == null || password.trim().isEmpty()) {
			Logger.warn("Zugang nicht gestattet für '" + login + "', da Passwort leer.");
			throw new UserMessage("Bitte Passwort eingeben!");
		}
		UserDAO dao = new UserDAO();
		if (dao.size() == 0) {
			Logger.warn("Leere User Collection lässt jeden Login zu!");
			return null;
		}
		password = UserService.hash(password, ".v0");
		User user = dao.byLogin(login);
		if (user == null) {
			Logger.warn("Zugang nicht gestattet für '" + login + "', da Benutzername falsch.");
			throw new UserMessage("Zugang nicht gestattet, da der Benutzer nicht bekannt ist."
					+ " Bitte gehe zurück und kontrolliere die Schreibweise des Benutzernamens.");
		}
		if (user.getKennwort() == null || user.getKennwort().isEmpty()) {
			// Passwort vergeben! Programmfehler!
			Logger.error("Programmfehler: Zugang nicht gestattet für '" + login + "', da der Benutzer kein Passwort hat.");
			throw new UserMessage("Benutzer nicht bekannt. Bitte an Administrator wenden.");
		}
		if (user.getKennwort().equals(password)) {
			return null; // Login erfolgreich
		}		
		Logger.warn("Zugang nicht gestattet für '" + login + "', da Passwort falsch.");
		throw new UserMessage("Zugang nicht gestattet, da das Passwort falsch ist. Bitte gehe zurück und gib das Passwort erneut ein."
				+ " Wende dich bitte an den Administrator falls Du dein Passwort vergessen haben solltest.");
	}

	@Override
	public boolean isRelevant(String service) {
		return "S".equals(service);
	}
}
