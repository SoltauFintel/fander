package de.mwvb.fander.startseite;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.PublicNoteEntry;
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
		//Logger.debug("Startseite Zustand: " + zustand.getClass().getSimpleName());
		
		put("h1title", zustand.getH1Title());
		put("jqueryOben", true);
		put("isAnsprechpartner", isAnsprechpartner());
		put("isUserManager", isUserManager());
		put("isDeveloper", isDeveloper());
		put("ansprechpartner", zustand.getAnsprechpartner());
		put("ansprechpartnerWeiblich", zustand.isAnsprechpartnerWeiblich());
		put("bestellungenErlaubt", false);
		put("wocheVorhanden", zustand.isWocheVorhanden());
		put("neueWocheHighlighted", zustand.isNeueWocheHighlighted());
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
		habenBestellt(woche);
		putPublicNotes();
	}

    private double addMenu(Zustand zustand, Woche woche) {
		final boolean zusatzstoffeAnzeigen = zustand.isZusatzstoffeAnzeigen();
		double summe = 0;
		boolean first = true;
		DataList menu = list("menu");
		for (Tag tag : woche.getTage()) {
			DataMap map = menu.add();
			map.put("wochentag", esc(tag.getWochentagText()));
			map.put("datum", getDatum(tag.getWochentag(), woche.getStartdatum()));
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
				addGericht(g, zusatzstoffeAnzeigen, tag.getWochentag(), map2);
				if (g.isBestellt()) {
					summe += g.getPreis();
				}
			}
		}
		return summe;
	}
    
    private String getDatum(int wt, String startdatum) {
        LocalDate d = LocalDate.parse(startdatum, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        d = d.plusDays(wt - 1);
        return d.getDayOfMonth() + "." + d.getMonthValue() + ".";
    }

	private void addGericht(Gericht g, boolean zusatzstoffeAnzeigen, int tag, DataMap map) {
		map.put("id", g.getId());
		map.put("titel", esc(getGerichtTitel(g, zusatzstoffeAnzeigen)));
		map.put("strikethru", isStrikethru(g, zusatzstoffeAnzeigen));
		map.put("zusatzstoffe", esc(g.getZusatzstoffe()));
		map.put("preis", g.getPreisFormatiert());
		map.put("preisJS", g.getPreisFormatiert().replace(",", "."));
		map.put("bestellt", g.isBestellt());
		map.put("wirdBestellt", g.isWirdBestellt());
		map.put("heuteBestellt", g.heuteBestellt(tag));
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
    
	/* TODO Die folgenden Methoden habe ich auf die Schnelle aus dem alten Code übernommen:
	   Die Fachlichkeit muss in die Zustand Klassen.
       Auch für obige Methoden prüfen. */
	
	private void habenBestellt(Woche woche) {
		putNamensliste("bestelltHaben", "Bestellt haben", "Bestellt hat", woche.getBestellungen().stream()
				.filter(m -> !m.getBestellungen().isEmpty())
				.map(Mitarbeiterbestellung::getUser));
		putNamensliste("absagen", "Absagen von", "Absage von", woche.getNichtBestellen().stream());
		putNamensliste("keineAussageVon", "Noch keine Aussagen von", "Noch keine Aussage von", keineAussageVon(woche));
	}
	
	private void putNamensliste(String var, String textPlural, String textSingular, Stream<String> namen) {
		String c = namen.sorted().collect(Collectors.joining(", "));
		if (!c.isEmpty()) {
			String text = c.contains(",") ? textPlural : textSingular;
			put(var, "<div>" + esc(text + ": " + c) + "</div>");
		} else {
			put(var, "");
		}
	}

	private Stream<String> keineAussageVon(Woche woche) {
		List<String> tb = new UserService().getTypischeBesteller();
		List<String> besteller = woche.getBestellungen().stream()
				.filter(m -> !m.getBestellungen().isEmpty())
				.map(Mitarbeiterbestellung::getUser)
				.collect(Collectors.toList());
		Set<String> absagen = woche.getNichtBestellen();
		List<String> ret = new ArrayList<>();
		for (String name : tb) {
			if (!besteller.contains(name) && !absagen.contains(name)) {
				ret.add(name);
			}
		}
		return ret.stream();
	}
	   
    private void putPublicNotes() {
        List<PublicNoteEntry> entries = new UserService().getPublicNotes();
        put("hasPublicNotes", !entries.isEmpty());
        DataList publicNotes = list("publicNotes");
        for (PublicNoteEntry e : entries) {
            DataMap map = publicNotes.add();
            map.put("text", esc(e.getPublicNote()));
            map.put("user", esc(e.getUser()));
            map.put("time", esc(getTime(e)));
        }
    }

    private String getTime(PublicNoteEntry e) {
        String time = "";
        if (e.getPublicNoteTimestamp() != null) {
            time = e.getPublicNoteTimestamp();
            int max = "2020-02-09 12:47".length();
            if (time.length() > max) {
                time = time.substring(0, max);
            }
        }
        return time;
    }
}
