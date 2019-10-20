package de.mwvb.fander.startseite;

import org.pmw.tinylog.Logger;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class NeuesDesign extends SAction {

	@Override
	protected void execute() {
		put("title", "Fander");
		info("Startseite (neues Design)");

		Zustand zustand = new StartseiteService().getZustand(user(), "1".equals(req.queryParams("m")));
		Logger.debug("Startseite Zustand: " + zustand.getClass().getSimpleName());
		put("h1title", zustand.getH1Title());
		put("jqueryOben", zustand.isJqueryOben());
		put("isAnsprechpartner", isAnsprechpartner());
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
		put("isUserManager", isUserManager());
		put("isDeveloper", isDeveloper());
	}
	
	private double addMenu(Zustand zustand, Woche woche) {
		final boolean zusatzstoffeAnzeigen = zustand.isZusatzstoffeAnzeigen();
		double summe = 0;
		boolean first = true;
		DataList menu = list("menu");
		for (Tag tag : woche.getTage()) {
			DataMap map = menu.add();
			map.put("wochentag", esc(tag.getWochentagText()));
			map.put("anzahlGerichte", "" + tag.getAnzahlGerichte());
			//map.put("hasBestellteGerichte", tag.hasBestellteGerichte());
			map.put("show", zustand.isShowAlwaysTag() || !tag.getGerichte().isEmpty());
			map.put("first", first);
			first = false;
			
			DataList gerichte = map.list("gerichte");
			for (Gericht g : tag.getGerichte()) {
				DataMap map2 = gerichte.add();
				addGericht(zusatzstoffeAnzeigen, g, map2);
				if (g.isBestellt()) {
					summe += g.getPreis();
				}
			}
		}
		return summe;
	}

	private void addGericht(boolean zusatzstoffeAnzeigen, Gericht g, DataMap map) {
		map.put("id", g.getId());
		String titel = g.getTitel();
		if (zusatzstoffeAnzeigen && g.getZusatzstoffe() != null && !g.getZusatzstoffe().isEmpty()) {
			titel += " (" + g.getZusatzstoffe() + ")";
		}
		map.put("titel", esc(titel));
		boolean strikethru = false;
		if (zusatzstoffeAnzeigen) {
			if (g.getZusatzstoffe() == null || "?".equals(g.getZusatzstoffe()) || g.getZusatzstoffe().toUpperCase().contains("A")) {
				strikethru = true;
			}
		}
		map.put("strikethru", strikethru);
		map.put("zusatzstoffe", esc(g.getZusatzstoffe()));
		map.put("preis", g.getPreisFormatiert());
		map.put("preisJS", g.getPreisFormatiert().replace(",", "."));
		map.put("bestellt", g.isBestellt());
		map.put("namen", g.getNamen());
		map.put("wirdBestellt", g.isWirdBestellt());
	}
}
