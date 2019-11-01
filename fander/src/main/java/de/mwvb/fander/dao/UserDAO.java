package de.mwvb.fander.dao;

import java.util.List;

import de.mwvb.fander.model.User;
import de.mwvb.maja.mongo.AbstractDAO;

public class UserDAO extends AbstractDAO<User> {

	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	@Override
	public List<User> list() {
		return createQuery()
				.order("user")
				.asList();
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
	
	public List<User> getTypischeBesteller() {
		return createQuery()
				.field("typischerBesteller").equal(true)
				.project("user", true)
				.order("user")
				.asList();
	}

    public User byToken(String token) {
        return createQuery().field("token").equal(token).field("aktiv").equal(true).get();
    }
}
