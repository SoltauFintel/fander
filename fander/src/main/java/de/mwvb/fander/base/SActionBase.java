package de.mwvb.fander.base;

import org.pmw.tinylog.Logger;

import com.google.common.base.Strings;

import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.maja.web.AppConfig;

public abstract class SActionBase extends ActionBase {
	public static final String TEXT_HTML = "text/html";

	public String user() {
		return AuthPlugin.getUser(req.session());
	}
	
	protected boolean isAdmin() {
		final String admin = new AppConfig().get("admin");
		if (admin != null) {
			final String user = user();
			for (String w : admin.split(",")) {
				if (w.trim().equalsIgnoreCase(user)) {
					return true;
				}
			}
		}
		return false;
	}
	
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
}
