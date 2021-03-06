package de.mwvb.fander.base;

import com.github.template72.data.DataMap;

import de.mwvb.fander.FanderApp;
import de.mwvb.fander.auth.Login2;
import de.mwvb.fander.auth.Roles;
import de.mwvb.maja.web.Action;

public abstract class SAction extends Action {
	public static final String TEXT_HTML = SActionBase.TEXT_HTML;
	public static final String CLASS = "Class: ";
	private Roles roles;

	protected void setTitle(String title) {
		if (title == null || title.trim().isEmpty()) {
			put("title", "Fander App");
		} else {
			put("title", title + " - Fander App");
		}
	}
	
	@Override
	public String run() {
		defGlobalVars(model);

		put("title", "Fander App");
		String user = user();
		put("user", user);
		put("hasUser", user != null && !user.trim().isEmpty());

		try {
			return super.run();
		} catch (Exception e) {
			// Manchmal (z.B. Template-Var-Fehler) kann man nicht erkennen woher der Fehler kommt, daher wird hier der ClassName ausgegeben.
			String msg = e.getMessage() + "\r\n" + CLASS + getClass().getName();
			if (!(e instanceof UserMessage)) {
				msg = e.getClass().getSimpleName() + ": " + msg;
			}
			throw new RuntimeException(msg, e);
		}
	}
	
	public static void defGlobalVars(DataMap model) {
		model.put("title", "Fander App");
		model.put("appversion", FanderApp.VERSION);
		model.put("jqueryOben", false);
		model.put("user", "");
		model.put("hasUser", false);
	}
	
	protected final void info(String msg) {
		SActionBase.info(user(), msg);
	}

	public final String user() {
	    return Login2.auth.getAnzeigenameVomEingeloggtenBenutzer(req.session());
	}
	
	protected final boolean isDeveloper() {
		return getRoles().isDeveloper(user());
	}
	
	protected final boolean isUserManager() {
		return getRoles().isUserManager(user());
	}
	
	protected final boolean isAnsprechpartner() {
		return getRoles().isAnsprechpartner(user());
	}
	
	private Roles getRoles() {
		if (roles == null) {
			roles = new Roles();
		}
		return roles;
	}
}
