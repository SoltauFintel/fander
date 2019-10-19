package de.mwvb.fander.dao;

import de.mwvb.fander.model.User;
import de.mwvb.maja.mongo.AbstractDAO;

public class UserDAO extends AbstractDAO<User> {

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	public User byUser(String user) {
		return createQuery()
				.field("user").equalIgnoreCase(user)
				.get();
	}
	
	public User byLogin(String login) {
		return createQuery()
				.field("login").equalIgnoreCase(login)
				.get();
	}
}
