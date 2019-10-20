package de.mwvb.fander.actions;

public class MyUserDataSave extends AbstractSaveUser {

	@Override
	protected void execute() {
		String login = req.queryParams("login");
		checkLogin(login);
		
		user = sv.getUser(user());
		
		changePassword(true);

		save(login);
		
		sv.save(user);
		info("MyUserDataSave: user saved");
		res.redirect("/");
	}
}
