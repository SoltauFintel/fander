package de.mwvb.fander.actions;

import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

// URL: /:startdatum
public class Bestellen extends SAction {

	@Override
	protected void execute() {
		setTitle("Bestellung eingeben");
		FanderService sv = new FanderService();
		Woche woche = sv.byStartdatum(req);
		trageBestellstatusEin(woche);

		new Woche2Model().toModel(woche, model, user());
		put("limit", getLimit(woche));
		put("jqueryOben", true);
		put("url", sv.getConfig().getUrl());
	}
	
	private void trageBestellstatusEin(Woche woche) {
		if (woche.getBestellungen() != null) {
			final String user = user();
			woche.getBestellungen().stream()
				.filter(b -> b.getUser().equalsIgnoreCase(user))
				.forEach(mb ->
					mb.getBestellungen().forEach(gb -> trageGerichtAlsBestelltEin(woche, gb.getGerichtId())));
		}
	}

	private void trageGerichtAlsBestelltEin(Woche woche, String gerichtId) {
		for (Tag tag : woche.getTage()) {
			for (Gericht g : tag.getGerichte()) {
				if (g.getId().equals(gerichtId)) {
					g.setBestellt(true);
					return;
				}
			}
		}
	}
	
	private String getLimit(Woche woche) {
		if (woche.getBestellungen() != null) {
			final String user = user();
			for (Mitarbeiterbestellung mb : woche.getBestellungen()) {
				if (mb.getUser().equalsIgnoreCase(user)) {
					if (mb.getLimit() != null) {
						return "" + mb.getLimit();
					}
					break;
				}
			}
		}
		return "";
	}
}
