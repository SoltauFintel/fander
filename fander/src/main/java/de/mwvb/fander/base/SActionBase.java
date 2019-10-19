package de.mwvb.fander.base;

import org.pmw.tinylog.Logger;

import com.google.common.base.Strings;

import de.mwvb.fander.service.PersonenService;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.web.ActionBase;

public abstract class SActionBase extends ActionBase {
	public static final String TEXT_HTML = "text/html";

	protected final void info(String msg) {
		info(user(), msg);
	}
	
	public static final void info(String user, String msg) {
		if (user == null) {
			user = "?";
		} else if (user.length() > 9) {
			user = user.substring(0, 9);
		}
		Logger.info(Strings.padEnd(user, 9, ' ') + " | " + msg);
	}

	protected final String user() {
		return AuthPlugin.getUser(req.session());
	}
	
	protected final boolean isDeveloper() {
		return PersonenService.isDeveloper(user());
	}
	
	protected final boolean isUserManager() {
		return PersonenService.isUserManager(user());
	}
	
	protected final boolean isAnsprechpartner() {
		return PersonenService.isAnsprechpartner(user());
	}
}
