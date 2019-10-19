package de.mwvb.fander.base;

import java.util.Collection;
import java.util.stream.Collectors;

import com.github.template72.data.DataMap;

import de.mwvb.fander.FanderApp;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.web.Action;

public abstract class SAction extends Action {
	public static final String TEXT_HTML = SActionBase.TEXT_HTML;
	public static final String CLASS = "Class: ";

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
		model.put("appversion", FanderApp.VERSION);
		model.put("jqueryOben", false);
	}
	
	public static void setGlobalVars(DataMap model) {
		defGlobalVars(model);

		model.put("hasUser", false);
	}

	public String user() {
		return AuthPlugin.getUser(req.session());
	}
	
	protected void combo(String id, Collection<String> texte, String selected) {
		put(id, texte.stream()
				.map(text -> (text.equals(selected) ? "<option selected>" : "<option>") + text + "</option>")
				.collect(Collectors.joining()));
	}
	
	protected final void info(String msg) {
		SActionBase.info(user(), msg);
	}
	
	protected boolean isAdmin() {
		return true; // TODO
	}
}
