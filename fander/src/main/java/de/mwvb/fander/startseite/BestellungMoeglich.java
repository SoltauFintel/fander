package de.mwvb.fander.startseite;

import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.FanderBestellungGericht;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public abstract class BestellungMoeglich extends Zustand {
	protected final FanderBestellung bestellung; // auch mit Einträgen von Tagen wo nichts bestellt werden wird, damit man sieht wer alles bestellt hat
	protected final FanderBestellung bestellungVorschau;

	public BestellungMoeglich(Woche woche, String user, FanderService sv) {
		super(woche, user);
		bestellung = sv.getFanderBestellung(woche, true);
		bestellungVorschau = sv.getFanderBestellung(woche, false);
		for (Tag tag : woche.getTage()) {
			for (Gericht g : tag.getGerichte()) {
				FanderBestellungGericht gg = bestellungVorschau.getFanderBestellungGericht(g.getId());
				g.setWirdBestellt(gg != null && gg.getAnzahl() > 0);
				
				gg = bestellung.getFanderBestellungGericht(g.getId());
				g.setNamen(calculateNamen(gg));
			}
		}
	}
	
	@Override
	public FanderBestellung getBestellungVorschau() {
		return bestellungVorschau;
	}
	
	private String calculateNamen(FanderBestellungGericht g) {
		return g.getBestellerText();
		//final String ich = getUser(); // ohne mich
		//return g == null ? "" : g.getBesteller().stream().filter(b -> !b.equals(ich)).collect(Collectors.joining(", "));
	}
}
