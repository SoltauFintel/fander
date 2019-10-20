package de.mwvb.fander.auth;

import java.util.List;
import java.util.stream.Collectors;

import de.mwvb.fander.dao.UserDAO;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.User;

public class UserService { // TODO rename to UserService
	private final UserDAO dao = new UserDAO();

	public List<User> list() {
		return dao.list();
	}
	
	public User byId(String id) {
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
}
