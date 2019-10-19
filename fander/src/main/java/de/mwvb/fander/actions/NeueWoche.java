package de.mwvb.fander.actions;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class NeueWoche extends SAction {
	boolean redirect = false;
	
	@Override
	protected void execute() {
		setTitle("Neue Fander Woche");
		FanderService sv = new FanderService();
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}
		
		// Im Woche-neu-erstellen-Modus wird erst die Woche gelöscht und dann diese Seite ohne force-Option aufgerufen,
		// damit durch F5 im Browser nicht versehentlich erneut die Woche angelegt wird.
		if ("1".equals(req.queryParams("force"))) {
			Woche vorh = sv.byStartdatum(sv.getNeueWocheStartdatum());
			if (vorh != null) {
				sv.delete(vorh);
				info("Neue Woche force -> Woche " + vorh.getStartdatum() + " gelöscht!");
			}
			redirect("/neue-woche");
			return;
		}
		
		Woche woche = sv.createNeueWoche();
		Woche vorh = sv.byStartdatum(woche.getStartdatum());
		if (vorh != null) {
			info("Neue Woche: Woche bereits vorhanden. Zeige Bestellseite an.");
			redirect("/" + vorh.getStartdatum());
			return;
		}
		if (woche.getTage() == null || woche.getTage().isEmpty()) {
			throw new UserMessage("Es konnte keine neue Woche gestartet werden, da das Menü leer ist!"
					+ " Möglicherweise ist die Speisekarte noch nicht verfügbar. Bitte versuche es später noch einmal.");
		}
		sv.save(woche);
		info("Neue Woche " + woche.getStartdatum() + " gespeichert. Gerichte: " + woche.getAnzahlGerichte());
		sv.alteWochenArchivieren(woche);

		new Woche2Model().toModel(woche, model, user());
		setTitle("Neue Woche " + woche.getStartdatum());
	}

	private void redirect(String url) {
		redirect = true;
		res.redirect(url);
	}
	
	// Missing Content Exceptions verhindern. Das ist ein Fehler in Maja.
	@Override
	protected String render() {
		if (redirect) {
			return "";
		}
		return super.render();
	}
}
