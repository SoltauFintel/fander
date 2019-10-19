package de.mwvb.fander.auth;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.auth.LogoutAction;
import de.mwvb.maja.auth.rememberme.RememberMeFeature;

public class SLogoutAction extends LogoutAction {
	
	public SLogoutAction(RememberMeFeature rememberMe) {
		super(rememberMe, true);
	}
	
	@Override
	protected void logLogout(String user, String userId) {
		Logger.info("Logout: " + user);
	}
}
