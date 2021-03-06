package de.mwvb.fander.startseite;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.FanderBestellungGericht;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.service.FanderService;

public class BestellungGeschlossen extends Zustand {
	private final Zustand zustand;
	
	public BestellungGeschlossen(Zustand zustand) {
		super(zustand.getWoche(), zustand.getUser());
		this.zustand = zustand;
		
		// kompakte Darstellung (Unsere Karte)
		FanderBestellung bestellungVorschau = zustand.getBestellungVorschau();
		for (Tag tag : zustand.getWoche().getTage()) {
			List<Gericht> kill = new ArrayList<>();
			for (Gericht g : tag.getGerichte()) {
				FanderBestellungGericht gg = bestellungVorschau.getFanderBestellungGericht(g.getId());
				if (gg == null || gg.getAnzahl() == 0) {
					kill.add(g);
				}
			}
			tag.getGerichte().removeAll(kill);
		}
	}
	
	@Override
	public boolean isShowBestellsumme(double summe) {
		return summe > 0;
	}
	
	@Override
	public String getH1Title() {
		if (zustand instanceof MoechteNichtBestellen) {
			return zustand.getH1Title();
		}
		return "Unsere Karte";
	}
	
	@Override
	protected boolean isShowAlwaysTag() {
		return false;
	}
	
	@Override
	public boolean isNeueWocheHighlighted() {
	    if (getWoche() == null) {
	        return true;
	    }
	    boolean isMonday = DayOfWeek.MONDAY.equals(LocalDate.now().getDayOfWeek());
        String startdatumDerAktuellenWoche = getWoche().getStartdatum();
        String heute = new FanderService().getNeueWocheStartdatum();
        return isMonday && !heute.equals(startdatumDerAktuellenWoche);
	}
}
