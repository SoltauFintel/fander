package de.mwvb.fander.auth;

import org.pmw.tinylog.Logger;

import com.github.template72.data.DataMap;

import de.mwvb.fander.base.SAction;
import de.mwvb.maja.auth.AuthFeature;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.auth.Authorization;
import de.mwvb.maja.auth.rememberme.RememberMeFeature;
import de.mwvb.maja.auth.rememberme.RememberMeInMongoDB;
import de.mwvb.maja.web.Action;
import de.mwvb.maja.web.ActionBase;
import spark.Request;
import spark.Response;

public class SAuth extends AuthPlugin {

	public SAuth() {
		super(new RememberMeInMongoDB() {
			@Override
			protected void logRememberedUser(String user, String userId) {
				Logger.info("Remembered user : " + user);
			}
		});
	}

	@Override
	public String login(Request req, Response res, String name, String foreignId, String service, boolean rememberMeWanted) {
		String msg = authorization.check(req, res, name, foreignId, service);
		if (msg != null) {
			return msg;
		}
		
		String longId = name.toLowerCase(); // Hier nicht das Passwort speichern. #maja-änderung: getLong() machen
		setLoginData(true, name, longId, req.session());
		rememberMe.rememberMe(rememberMeWanted, res, name, longId);
		if (isDebugLogging()) {
			logLogin(name, longId);
		}

		// Redirect zur ursprünglich angewählten Seite
		String uri = req.session().attribute("uri");
		if (uri == null || uri.isEmpty()) {
			uri = "/";
		}
		req.session().removeAttribute("uri");
		res.redirect(uri);
		return "";
	}

	@Override
	protected AuthFeature getFeature() {
		SAuthFeature f = new SAuthFeature();
		f.init(this);
		return f;
	}
	
	@Override
	protected Authorization getAuthorization() {
		return new SAuthorization();
	}
	
	@Override
	protected String renderLoginPage() {
		DataMap model = new DataMap();
		model.put("user", "");
		model.put("pw", "");
		model.put("title", "Login - Fander App");
		SAction.defGlobalVars(model); 
		return Action.templates.render("login", model);
	}
	
	@Override
	protected void logLogin(String name, String longId) {
		Logger.info("Login : " + name);
	}
	
	@Override
	protected ActionBase getLogoutAction(RememberMeFeature rememberMe) {
		return new SLogoutAction(rememberMe);
	}
}
