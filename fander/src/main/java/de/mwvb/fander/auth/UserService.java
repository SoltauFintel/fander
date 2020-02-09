package de.mwvb.fander.auth;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.PublicNoteEntry;
import de.mwvb.fander.model.User;
import de.mwvb.fander.rest.LoginRequestJSON;
import de.mwvb.fander.rest.LoginResponseJSON;
import de.mwvb.maja.mongo.AbstractDAO;
import de.mwvb.maja.web.AppConfig;

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
			user.setKennwort(UserService.hash("fander", ".v0"));
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
	
	public User byToken(String token) {
	    return dao.byToken(token);
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

	/**
	 * @param p zu chiffrierendes Klartext Kennwort
	 * @param version z.B. ".v0"
	 * @return chiffriertes Kennwort
	 */
	public static String hash(String p, String version) {
		try {
			int repeats = Integer.parseInt(new AppConfig().get("password-hash-repeats" + version, "1"));
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] bytes = p.getBytes(StandardCharsets.UTF_8);
			for (int i = 0; i < repeats; i++) {
				bytes = md.digest(bytes);
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(String.format("%02x", bytes[i]));
			}
			return sb.toString() + version;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public LoginResponseJSON login(LoginRequestJSON lr) {
        User user = new UserDAO().byLogin(lr.getLogin());
        if (user == null) {
            throw new RuntimeException("E1: Bitte überprüfe den Login!");
        }
        if (!user.isAktiv()) {
            throw new RuntimeException("E2: Benutzerzugang gesperrt!");
        }
        if (!user.getKennwort().equals(lr.getPassword())) {
            throw new RuntimeException("E3: Bitte überprüfe das Kennwort!");
        }
        if (user.getToken() != null && "-".equals(user.getToken())) {
            throw new RuntimeException("E4: REST API für Benutzer gesperrt!");
        }
        if (user.getToken() == null || user.getToken().isEmpty() || lr.isForceNewToken()) {
            user.setToken(AbstractDAO.genId());
            dao.save(user);
            Logger.info("Neuer Token für User " + lr.getLogin() + " generiert.");
        }
        LoginResponseJSON r = new LoginResponseJSON();
        r.setToken(user.getToken());
        return r;
	}
	
	public void logout(User user) {
	    if (!"-".equals(user.getToken())) {
    	    user.setToken(null);
    	    dao.save(user);
	    }
	}

    public List<PublicNoteEntry> getPublicNotes() {
        List<PublicNoteEntry> ret = new ArrayList<>();
        for (User user : dao.getPublicNotes()) {
            if (user.getPublicNote() != null && !user.getPublicNote().trim().isEmpty()) {
                PublicNoteEntry e = new PublicNoteEntry();
                e.setUser(user.getUser());
                e.setPublicNote(user.getPublicNote());
                e.setPublicNoteTimestamp(user.getPublicNoteTimestamp());
                ret.add(e);
            }
        }
        return ret;
    }
}
