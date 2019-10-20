package de.mwvb.fander.startseite;

import org.pmw.tinylog.Logger;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class Index extends SAction {

	@Override
	protected void execute() {
		put("title", "Fander");
		info("Startseite");

		boolean bestellungAendern = "1".equals(req.queryParams("m"));
		
		Zustand zustand = new StartseiteService().getZustand(user(), bestellungAendern);
		Logger.debug("Startseite Zustand: " + zustand.getClass().getSimpleName());
		
		put("h1title", zustand.getH1Title());
		put("jqueryOben", zustand.isJqueryOben());
		put("isAnsprechpartner", isAnsprechpartner());
		put("isUserManager", isUserManager());
		put("isDeveloper", isDeveloper());
		put("ansprechpartner", zustand.getAnsprechpartner());
		put("ansprechpartnerWeiblich", zustand.isAnsprechpartnerWeiblich());
		put("wocheVorhanden", zustand.isWocheVorhanden());
		if (!zustand.isWocheVorhanden()) {
			return;
		}
		Woche woche = zustand.getWoche();

		put("startdatum", woche.getStartdatum());
		put("startdatumNice", woche.getStartdatumNice());
		put("bestellmodus", zustand.isBestellmodus());
		put("bestellungenErlaubt", woche.isBestellungenErlaubt());
		put("showBestellungAendern", zustand.isShowBestellungAendern());
		put("showDochBestellen", zustand.isShowDochBestellen());
		double summe = addMenu(zustand, woche);
		String summeText = FanderService.format(summe);
		put("summe", summeText);
		put("summeJS", summeText.replace(",", "."));
		put("limit", zustand.getLimit());
		put("showBestellsumme", zustand.isShowBestellsumme(summe));
	}
	
	private double addMenu(Zustand zustand, Woche woche) {
		final boolean zusatzstoffeAnzeigen = zustand.isZusatzstoffeAnzeigen();
		double summe = 0;
		boolean first = true;
		DataList menu = list("menu");
		for (Tag tag : woche.getTage()) {
			DataMap map = menu.add();
			map.put("wochentag", esc(tag.getWochentagText()));
			map.put("show", zustand.showTag(tag));
			if (first && !tag.getGerichte().isEmpty()) {
				map.put("first", first);
				first = false;
			} else {
				map.put("first", false);
			}
			
			DataList gerichte = map.list("gerichte");
			for (Gericht g : tag.getGerichte()) {
				DataMap map2 = gerichte.add();
				addGericht(g, zusatzstoffeAnzeigen, map2);
				if (g.isBestellt()) {
					summe += g.getPreis();
				}
			}
		}
		return summe;
	}

	private void addGericht(Gericht g, boolean zusatzstoffeAnzeigen, DataMap map) {
		map.put("id", g.getId());
		map.put("titel", esc(getGerichtTitel(g, zusatzstoffeAnzeigen)));
		map.put("strikethru", isStrikethru(g, zusatzstoffeAnzeigen));
		map.put("zusatzstoffe", esc(g.getZusatzstoffe()));
		map.put("preis", g.getPreisFormatiert());
		map.put("preisJS", g.getPreisFormatiert().replace(",", "."));
		map.put("bestellt", g.isBestellt());
		map.put("wirdBestellt", g.isWirdBestellt());
		map.put("namen", esc(g.getNamen()));
	}

	private String getGerichtTitel(Gericht g, boolean zusatzstoffeAnzeigen) {
		String titel = g.getTitel();
		if (zusatzstoffeAnzeigen && g.getZusatzstoffe() != null && !g.getZusatzstoffe().isEmpty()) {
			titel += " (" + g.getZusatzstoffe() + ")";
		}
		return titel;
	}

	private boolean isStrikethru(Gericht g, boolean zusatzstoffeAnzeigen) {
		if (zusatzstoffeAnzeigen) {
			if (g.getZusatzstoffe() == null || "?".equals(g.getZusatzstoffe()) || g.getZusatzstoffe().toUpperCase().contains("A")) {
				return true;
			}
		}
		return false;
	}
}
