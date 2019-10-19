package de.mwvb.fander.auth;

import de.mwvb.maja.web.ActionBase;

public class Login2 extends ActionBase {
	public static SAuth auth;
	
	@Override
	protected void execute() {
		String user = req.queryParams("user");
		String pw = req.queryParams("pw");
		if (user == null || pw == null) {
			throw new RuntimeException("Es wurden nicht alle Argumente angegeben!");
		}
		
		auth.login(req, res, user, pw.trim(), "S", true);
	}
}
