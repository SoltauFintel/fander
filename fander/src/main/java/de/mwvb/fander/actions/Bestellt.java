package de.mwvb.fander.actions;

import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class Bestellt extends SAction {

	@Override
	protected void execute() {
		setTitle("Bestellung ansehen");
		FanderService sv = new FanderService();
		Woche woche = sv.getJuengsteWoche();
		Mitarbeiterbestellung mb = sv.getMitarbeiterbestellung(woche, user());
		sv.bestellteGerichteVerzeichnen(woche, mb);

		new Woche2Model().toModel(woche, model, user());
		limitinfo(mb);
	}

	private void limitinfo(Mitarbeiterbestellung mb) {
		String limitinfo = "";
		int n = Integer.MAX_VALUE;
		int max = mb.getBestellungen().size();
		if (mb.getLimit() != null) {
			n = mb.getLimit().intValue();
		}
		if (n > max) {
			n = max;
		}
		if (n == 1) {
			limitinfo = "Es wird für dich maximal 1 Gericht bestellt.";
		} else {
			limitinfo = "Es werden für dich maximal " + n + " Gerichte bestellt.";
		}
		put("hasLimitinfo", !limitinfo.isEmpty());
		put("limitinfo", limitinfo);
	}
}
