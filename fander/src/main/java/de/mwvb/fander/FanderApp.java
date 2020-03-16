package de.mwvb.fander;

import java.util.Date;

import org.pmw.tinylog.Logger;

import com.github.template72.compiler.CompiledTemplates;
import com.github.template72.compiler.TemplateCompiler;
import com.github.template72.compiler.TemplateCompilerBuilder;
import com.github.template72.loader.ResourceTemplateLoader;
import com.github.template72.loader.TemplateFileCache;
import com.github.template72.loader.TemplateLoader;

import de.mwvb.fander.actions.Bestellen;
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
import de.mwvb.fander.actions.MyUserData;
import de.mwvb.fander.actions.MyUserDataSave;
import de.mwvb.fander.actions.NeueWoche;
import de.mwvb.fander.actions.NeueWocheForce;
import de.mwvb.fander.actions.NichtBestellen;
import de.mwvb.fander.actions.PublicNote;
import de.mwvb.fander.actions.PublicNoteSave;
import de.mwvb.fander.actions.UnsereKarte;
import de.mwvb.fander.actions.UnsereKarteDruck;
import de.mwvb.fander.actions.UserEdit;
import de.mwvb.fander.actions.UserSave;
import de.mwvb.fander.actions.Users;
import de.mwvb.fander.actions.Wochen;
import de.mwvb.fander.auth.Login2;
import de.mwvb.fander.auth.SAuth;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.MyErrorPage;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.rest.BestellenREST;
import de.mwvb.fander.rest.LoginREST;
import de.mwvb.fander.rest.LogoutREST;
import de.mwvb.fander.rest.NichtBestellenREST;
import de.mwvb.fander.rest.ReadPublicNoteREST;
import de.mwvb.fander.rest.UnsereKarteREST;
import de.mwvb.fander.rest.WritePublicNoteREST;
import de.mwvb.fander.startseite.Index;
import de.mwvb.maja.mongo.Database;
import de.mwvb.maja.web.AbstractWebApp;
import de.mwvb.maja.web.Action;
import de.mwvb.maja.web.ActionBase;
import de.mwvb.maja.web.AppConfig;
import spark.Spark;

public class FanderApp extends AbstractWebApp {
	public static final String VERSION = "1.04.0";
	
	public static void main(String[] args) {
		new FanderApp().start(VERSION);
	}
	
	@Override
	protected void routes() {
		_get("/", Index.class);

		// Developer
		_get ("/wochen", Wochen.class);
		_get ("/delete", DeleteWoche.class);

		// User-Manager
		_get ("/config", FanderConfigAction.class);
		_post("/config", FanderConfigSaveAction.class);
		_get ("/users/:id", UserEdit.class);
		_post("/users/:id", UserSave.class);
		_get ("/users", Users.class);
		
		// Ansprechpartner
		_get ("/neue-woche", NeueWoche.class);
		_get ("/neue-woche-force", NeueWocheForce.class);
		_get ("/infomails", FanderMailAction.class);
		_post("/infomails", FanderMailSendAction.class);
		_get ("/infomails-sent", FanderMailSentAction.class);
		_get ("/close/:startdatum", BestellungClosen.class);
		_get ("/anruf", FanderAnruf.class);
		_get ("/status/:startdatum", FanderBestellstatusAction.class);
		
		// User
		_get ("/myuserdata", MyUserData.class);
		_post("/myuserdata", MyUserDataSave.class);
		_get ("/nicht-bestellen/:startdatum", NichtBestellen.class);
		_get ("/bestellen", Bestellen.class);
		_post("/bestellen/:startdatum", BestellungAbsenden.class);
		_get ("/bestellt", Bestellt.class);
		_get ("/unsere-karte", UnsereKarte.class);
		_get ("/unsere-karte-druck", UnsereKarteDruck.class);
		_get ("/publicnote", PublicNote.class);
		_post("/publicnote", PublicNoteSave.class);
		
		_post("/rest/login", LoginREST.class);
		_get ("/rest/logout", LogoutREST.class);
		_get ("/rest/unsere-karte", UnsereKarteREST.class);
		_post("/rest/bestellen/:startdatum", BestellenREST.class);
		_get ("/rest/nicht-bestellen/:startdatum", NichtBestellenREST.class);
		_get ("/rest/public-note", ReadPublicNoteREST.class);
		_post("/rest/public-note", WritePublicNoteREST.class);
		Spark.get("/rest/version", (res, req) -> VERSION);
	}
	
	@Override
	protected void initStaticFileLocation() {
		super.initStaticFileLocation();
		long expireTimeSeconds = 365 * 24 * 60 * 60; // 1 Jahr
		Spark.staticFiles.header("Cache-Control", "public, max-age=" + expireTimeSeconds);
		Spark.staticFiles.header("Expires", new Date(System.currentTimeMillis() + (expireTimeSeconds * 1000)).toString());
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
				"publicnote",
				"sent",
				"unserekarte",
				"unserekartedruck",
				"user",
				"users",
				"wochen"
				);
		System.out.println("Templates compiled");

		auth = new SAuth();
		Login2.auth = (SAuth) auth;
		auth.addNotProtected("/rest");
		
		// Alle Benutzer exportieren
		String dn = new AppConfig().get("dump-users");
		if (dn != null && !dn.isEmpty()) {
			Logger.warn("Alle Benutzer werden exportert. Der AppConfig-Key \"dump-users\" sollte normalerweise nicht gesetzt sein!");
			new UserService().dumpUsers(dn);
		}
	}

	@Override
	protected ActionBase getErrorPage() {
		return new MyErrorPage();
	}
}
