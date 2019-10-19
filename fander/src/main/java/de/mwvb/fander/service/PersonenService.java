package de.mwvb.fander.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.User;
import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.web.AppConfig;

public class PersonenService {
	private final List<User> users = new ArrayList<>();
	
	public PersonenService() {
		// Zukünftig sollen die Personen aus der DB kommen.
		AppConfig c = new AppConfig();
		int i = 1;
		while (true) {
			String key = "" + i;
			if (key.length() < 2) {
				key = "0" + key;
			}
			key = "user" + key;
			String line = c.get(key);
			i++;
			if (line == null) {
				break;
			} else if (line.isEmpty()) {
				continue;
			}
			while (line.contains("  ")) {
				line = line.replace("  ", " ");
			}
			String w[] = line.replace("  ", " ").split(" ");
			User user = new User();
			user.setInfomail(line.contains(" ausgewählt"));
			user.setTypischerBesteller(line.contains(" tb"));
			user.setWeiblich("W".equalsIgnoreCase(w[0]));
			user.setVorname(w[1].replace("_", " "));
			user.setNachname(w[2].replace("_", " "));
			if (w[3].isEmpty()) {
				throw new RuntimeException("Kennwort leer!");
			}
			user.setKennwort(w[3]);
			user.setZusatzstoffeAnzeigen(line.contains(" Zusatzstoffe"));
			user.setUser(user.getVorname());
			users.add(user);
		}
		users.sort((a, b) -> a.getUser().toLowerCase().compareTo(b.getUser().toLowerCase()));
	}
	
	List<User> getUsers() {
		return users;
	}

	private User find(String user) {
		for (User p : users) {
			if (p.getUser().equalsIgnoreCase(user)) {
				return p;
			}
		}
		throw new RuntimeException("User '" + user + "' nicht vorhanden!");
	}

	public List<String> getTypischeBesteller() {
		return users.stream().filter(p -> p.isTypischerBesteller()).map(p -> p.getUser()).collect(Collectors.toList());
	}
	
	public Map<String, String> getLogins() {
		Map<String, String> ret = new HashMap<>();
		for (User p : users) {
			ret.put(p.getUser(), p.getKennwort());
		}
		return ret;
	}
	
	public List<String> getMitarbeiterliste() {
		return users.stream().map(p -> p.getUser()).collect(Collectors.toList());
	}

	public List<MailEmpfaenger> getMailEmpfaenger() {
		return this.users.stream().map(person -> {
			MailEmpfaenger empfaenger = new MailEmpfaenger();
			empfaenger.setName(person.getUser());
			empfaenger.setId(empfaenger.getName().toLowerCase().replace("ü", "ue").replace("ä", "ae").replace("ö", "oe"));
			empfaenger.setAusgewaehlt(person.isInfomail());
			return empfaenger;
		}).collect(Collectors.toList());
	}
	
	public String getEmailadresse(String user) {
		try {
			return find(user).getEmailadresse() + new AppConfig().get("mail.postfix", "");
		} catch (Exception e) {
			return null;
		}
	}

	public String macheLang(String user) {
		try {
			return find(user).getName();
		} catch (Exception e) {
			// Es müssen derzeit nicht alle User unterstützt werden.
			return user;
		}
	}
	
	public boolean weiblich(String user) {
		return find(user).isWeiblich();
	}
	
	public boolean zusatzstoffeAnzeigen(String user) {
		return find(user).isZusatzstoffeAnzeigen();
	}

	/**
	 * @param user i.d.R. akt. User
	 * @return true: darf alle Developer-Funktionen ausführen, z.B. Anmelden als
	 */
	public static boolean isDeveloper(final String user) {
		return isRole(user, "developer");
	}

	/**
	 * @param user i.d.R. akt. User
	 * @return true: darf Benutzer verwalten und Einstellungen vornehmen
	 */
	public static boolean isUserManager(String user) {
		return isRole(user, "user-manager");
	}
	
	private static boolean isRole(final String user, String configKey) {
		final String users = new AppConfig().get(configKey);
		if (users != null) {
			for (String w : users.split(",")) {
				if (w.trim().equalsIgnoreCase(user)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param user i.d.R. akt. User
	 * @return true: darf Woche starten und Bestellung schließen
	 */
	public static boolean isAnsprechpartner(String user) {
		FanderConfig config = new FanderService().getConfig();
		return user.equalsIgnoreCase(config.getAdmin());
	}
	
	public void saveUsers() {
		UserDAO dao = new UserDAO();
		if (!dao.list().isEmpty()) {
			throw new RuntimeException("User Liste muss in der Datenbank leer sein!");
		}
		String mailPostfix = new AppConfig().get("mail.postfix", "");
		for (User user : users) {
			user.setId(AbstractDAO.id6());
			user.setLogin(user.getUser());
			user.setEmailadresse(user.getEmailadresse() + mailPostfix);
			user.setKennwort(User.hash(user.getKennwort()));
			dao.save(user);
		}
		SActionBase.info("?", "User erstmalig in Datenbank gespeichert: " + users.size());
	}
}
