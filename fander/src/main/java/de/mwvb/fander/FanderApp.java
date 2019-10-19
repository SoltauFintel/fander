package de.mwvb.fander;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.compiler.TemplateCompiler;
import com.github.template72.compiler.TemplateCompilerBuilder;
import com.github.template72.loader.ResourceTemplateLoader;
import com.github.template72.loader.TemplateFileCache;
import com.github.template72.loader.TemplateLoader;

import de.mwvb.fander.actions.Bestellen;
import de.mwvb.fander.actions.BestellenRedirect;
import de.mwvb.fander.actions.Bestellt;
import de.mwvb.fander.actions.BestellungAbsenden;
import de.mwvb.fander.actions.BestellungClosen;
import de.mwvb.fander.actions.DeleteWoche;
import de.mwvb.fander.actions.FanderAnruf;
import de.mwvb.fander.actions.FanderBestellstatusAction;
import de.mwvb.fander.actions.FanderConfigAction;
import de.mwvb.fander.actions.FanderConfigSaveAction;
import de.mwvb.fander.actions.FanderMailAction;
import de.mwvb.fander.actions.FanderMailSendAction;
import de.mwvb.fander.actions.FanderMailSentAction;
import de.mwvb.fander.actions.Index;
import de.mwvb.fander.actions.MyUserData;
import de.mwvb.fander.actions.MyUserDataSave;
import de.mwvb.fander.actions.NeueWoche;
import de.mwvb.fander.actions.NeueWocheForce;
import de.mwvb.fander.actions.NichtBestellen;
import de.mwvb.fander.actions.UnsereKarte;
import de.mwvb.fander.actions.UnsereKarteDruck;
import de.mwvb.fander.actions.UnsereKarteRedirect;
import de.mwvb.fander.actions.Wochen;
import de.mwvb.fander.auth.Login2;
import de.mwvb.fander.auth.SAuth;
import de.mwvb.fander.base.MyErrorPage;
import de.mwvb.fander.model.Woche;
import de.mwvb.maja.mongo.Database;
import de.mwvb.maja.web.AbstractWebApp;
import de.mwvb.maja.web.Action;
import de.mwvb.maja.web.ActionBase;

public class FanderApp extends AbstractWebApp {
	public static final String VERSION = "1.00.2";
	
	public static void main(String[] args) {
		new FanderApp().start(VERSION);
	}
	
	@Override
	protected void routes() {
		_get("/", Index.class);

		_get ("/config", FanderConfigAction.class);
		_post("/config-save", FanderConfigSaveAction.class);
		_get ("/wochen", Wochen.class);
		_get ("/neue-woche", NeueWoche.class);
		_get ("/bestellen", BestellenRedirect.class);
		_get ("/unsere-karte", UnsereKarteRedirect.class);
		_get ("/karte", UnsereKarteRedirect.class);
		_get ("/mail/sent", FanderMailSentAction.class);
		_get ("/myuserdata", MyUserData.class);
		_post("/save-myuserdata", MyUserDataSave.class);

		_get ("/:startdatum/force", NeueWocheForce.class);
		_post("/:startdatum/mail/send", FanderMailSendAction.class);
		_get ("/:startdatum/mail", FanderMailAction.class);
		_post("/:startdatum/bestellung-absenden", BestellungAbsenden.class);
		_get ("/:startdatum/bestellt", Bestellt.class);
		_get ("/:startdatum/close", BestellungClosen.class);
		_get ("/:startdatum/anruf", FanderAnruf.class);
		_get ("/:startdatum/unsere-karte-druck", UnsereKarteDruck.class);
		_get ("/:startdatum/unsere-karte", UnsereKarte.class);
		_get ("/:startdatum/status", FanderBestellstatusAction.class);
		_get ("/:startdatum/delete", DeleteWoche.class);
		_get ("/:startdatum/:user/:nichtBestellen", NichtBestellen.class);
		_get ("/:startdatum", Bestellen.class);
	}
	
	@Override
	protected void initDatabase() {
		System.out.println("Connecting to database...");
		Database.open(Woche.class);
	}
	
	@Override
	protected void init() {
		TemplateLoader loader = new ResourceTemplateLoader() {
			@Override
			public String charsetName() {
				return "UTF-8"; // Umlaute Darstellung unter Windows sicherstellen
			}
		};
		TemplateCompiler compiler = new TemplateCompilerBuilder().withLoader(loader).build();
		Action.templates = new CompiledTemplates(compiler, new TemplateFileCache(), config.isDevelopment(),
				"anruf",
				"bestellen",
				"bestellt",
				"config",
				"fandermailaction",
				"index",
				"login",
				"myerrorpage",
				"myuserdata",
				"neuewoche",
				"neuewocheforce",
				"sent",
				"unserekarte",
				"unserekartedruck",
				"wochen"
				);
		System.out.println("Templates compiled");

		auth = new SAuth();
		Login2.auth = (SAuth) auth;
	}

	@Override
	protected ActionBase getErrorPage() {
		return new MyErrorPage();
	}
}
