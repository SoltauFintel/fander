package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.auth.Roles;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.User;

public class Users extends SAction {

	@Override
	protected void execute() {
		if (!isUserManager()) {
			throw new AuthException();
		}
		setTitle("Benutzerverwaltung");
		info("Benutzerverwaltung");
		Roles roles = new Roles();
		DataList list = list("users");
		for (User user : new UserService().list()) {
			DataMap map = list.add();
			map.put("id", user.getId());
			map.put("user", user.getUser());
			map.put("login", user.getLogin());
			map.put("name", user.getName());
			map.put("emailadresse", user.getEmailadresse());
			map.put("rollen", roles.getRollen(user.getUser()));
		}
	}
}
