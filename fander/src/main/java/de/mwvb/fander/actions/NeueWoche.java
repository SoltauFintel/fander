package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class NeueWoche extends SAction {
	private boolean redirect = false;
	
	@Override
	protected void execute() {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}
		
		if ("1".equals(req.queryParams("force"))) {
			forceNeueWoche();
		} else {
			neueWoche();
		}
	}
	
	private void neueWoche() {
		FanderService sv = new FanderService();
		Woche vorh = sv.byStartdatum(sv.getNeueWocheStartdatum());
		if (vorh == null) {
			Woche woche = sv.createNeueWoche(true);
			sv.save(woche);
			info("Neue Woche " + woche.getStartdatum() + " gespeichert. Gerichte: " + woche.getAnzahlGerichte());
			sv.alteWochenArchivieren(woche);
	
			new Woche2Model().toModel(woche, model, user());
			setTitle("Neue Woche " + woche.getStartdatum());
		} else {
			info("Neue Woche: Woche bereits vorhanden.");
		}
		redirect("/");
	}
	
	// Im Woche-neu-erstellen-Modus wird erst die Woche gelöscht und dann diese Seite ohne force-Option aufgerufen,
	// damit durch F5 im Browser nicht versehentlich erneut die Woche angelegt wird.
	private void forceNeueWoche() {
		FanderService sv = new FanderService();
		String s = sv.getNeueWocheStartdatum();
		if (sv.delete(s)) {
			info("Erstellung neuer Woche erzwingen -> Woche " + s + " gelöscht!");
		} else {
			info("Erstellung neuer Woche erzwingen -> Woche " + s + " gab es gar nicht.");
		}
		redirect("/neue-woche");
	}

	private void redirect(String url) {
		redirect = true;
		res.redirect(url);
	}
	
	// TODO Missing Content Exceptions verhindern. Das ist ein Fehler in Maja.
	@Override
	protected String render() {
		if (redirect) {
			return "";
		}
		return super.render();
	}
}
