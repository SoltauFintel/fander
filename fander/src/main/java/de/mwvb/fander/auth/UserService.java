package de.mwvb.fander.auth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.User;
import de.mwvb.maja.mongo.AbstractDAO;

public class UserService {
	public static final String CREATE = "create";
	private final UserDAO dao = new UserDAO();

	public List<User> list() {
		return dao.list();
	}
	
	public User byId(String id) {
		if (CREATE.equals(id)) {
			User user = new User();
			user.setId(AbstractDAO.id6());
			user.setUser("User_" + user.getId());
			user.setLogin(user.getUser());
			user.setKennwort(User.hash("fander"));
			return user;
		}
		return dao.get(id);
	}
	
	public User getUser(String user) {
		User u = dao.byUser(user);
		if (u == null) {
			throw new RuntimeException("User '" + user + "' nicht vorhanden!");
		}
		return u;
	}

	public List<String> getTypischeBesteller() {
		return dao.getTypischeBesteller()
				.stream()
				.map(user -> user.getUser())
				.collect(Collectors.toList());
	}
	
	public List<MailEmpfaenger> getMailEmpfaenger() {
		return dao.list().stream()
				.filter(person -> person.getEmailadresse() != null && person.getEmailadresse().contains("@"))
				.map(person -> {
					MailEmpfaenger empfaenger = new MailEmpfaenger();
					empfaenger.setId(person.getId());
					empfaenger.setName(person.getUser());
					empfaenger.setEmailadresse(person.getEmailadresse());
					empfaenger.setAusgewaehlt(person.isInfomail());
					return empfaenger;
				})
				.collect(Collectors.toList());
	}
	
	/**
	 * @param user User.user
	 * @return User.name, im Fehlerfall user
	 */
	public String macheLang(String user) {
		try {
			return getUser(user).getName();
		} catch (Exception e) {
			return user;
		}
	}
	
	public void save(User user) {
		dao.save(user);
	}
	
	public void dumpUsers(String dn) {
		File f = new File(dn);
		try (FileWriter w = new FileWriter(f)) {
			w.write("Id\tUser\tLogin\tVorname\tNachname\tEmailadresse\tweiblich\tInfomail\ttypischerBesteller\tZusatzstoffe\tKennwort\r\n");
			for (User user : list()) {
				w.write(user.getId() + "\t");
				w.write(user.getUser() + "\t");
				w.write(user.getLogin() + "\t");
				w.write(user.getVorname() + "\t");
				w.write(user.getNachname() + "\t");
				w.write(user.getEmailadresse() + "\t");
				w.write(user.isWeiblich() + "\t");
				w.write(user.isInfomail() + "\t");
				w.write(user.isTypischerBesteller() + "\t");
				w.write(user.isZusatzstoffeAnzeigen() + "\t");
				w.write(user.getKennwort() + "\r\n");
			}
		} catch (IOException e) {
			Logger.error("Fehler beim Exportieren der Benutzer", e);
			return;
		}
		Logger.info("Alle Benutzer exportiert in Datei: " + f.getAbsolutePath());
	}
}
