package de.mwvb.fander.actions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Gerichtbestellung;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;
import de.mwvb.fander.service.PersonenService;

// TODO Tagesmenü oben in einer Box darstellen. Der jetzige Text davor wird zur Boxüberschrift. Oder alle pers. Gerichte der Woche anzeigen und
//      den aktuellen Tag hervorheben.
public class Index extends SAction {

	@Override
	protected void execute() {
		put("title", "Fander");
		info("Startseite");
		meinBestellstatus();
		put("isUserManager", isUserManager());
	}

	public void meinBestellstatus() {
		put("bestellungVorhandenUndAenderbar", false);
		put("bestellt", false);
		put("nichtBestellen", false);
		put("nichtBestellenUndo", false);
		put("zeigeBestellenButton", false);
		put("ansprechpartner", "");
		put("absagen", "");
		put("bestelltHaben", "");
		put("keineAussageVon", "");
		put("hasFuerHeuteBestellt", false);

		String status = null;
		FanderService sv = new FanderService();
		put("isFanderAdmin", isAnsprechpartner());
		Woche woche = sv.getJuengsteWoche();
		if (woche == null) {
			status = "Es ist keine Bestellung möglich.";
			put("startdatum", "");
			put("startdatumNice", "");
			put("hasWoche", false);
		} else {
			status = wocheVorhanden(sv, isAnsprechpartner(), woche, user());
		}		
		put("meinBestellstatus", status);
		put("montag", isMontag(woche));
	}

	private String wocheVorhanden(FanderService sv, boolean isFanderAdmin, Woche woche, final String user) {
		String status;
		put("hasWoche", true);
		put("startdatum", woche.getStartdatum());
		put("startdatumNice", woche.getStartdatumNice());
		put("bestellungenErlaubt", woche.isBestellungenErlaubt());
		put("bestellt", woche.isBestellt());
		boolean showAnsprechpartner = false;
		Mitarbeiterbestellung mb = sv.getMitarbeiterbestellung(woche, user);
		if (mb == null || mb.getBestellungen().isEmpty()) { // User hat nicht bestellt.
			boolean nb = woche.getNichtBestellen().contains(user);
			if (nb) {
				status = "";
			} else if (woche.isBestellungenErlaubt()) {
				status = "Du hast noch nichts bestellt.";
				put("zeigeBestellenButton", true);
			} else {
				status = "Du hast nichts bestellt. Bestellungen sind nicht mehr möglich.";
			}
			showAnsprechpartner = !nb;
			put("nichtBestellen", !nb);
			put("nichtBestellenUndo", nb);
		} else { // User hat bestellt.
			int n = mb.getBestellungen().size();
			status = "Du hast <a href=\"/essen/fander/" + woche.getStartdatum() + "/bestellt\">" + n + " Gericht" + (n == 1 ? "" : "e") + "</a> bestellt.";
			wasHeuteBestellt(woche, mb);
			if (woche.isBestellungenErlaubt()) {
				put("bestellungVorhandenUndAenderbar", true);
			} else {
				status += " Die Bestellung wurde geschlossen. <a href=\"/unsere-karte\" class=\"btn btn-default\" style=\"margin-left: 1em\">"
						+ "<i class=\"fa fa-cutlery\"></i> Unsere Karte</a>";
			}
			showAnsprechpartner = true;
		}
		put("showAnsprechpartner", showAnsprechpartner);
		if (showAnsprechpartner) {
			String ans = sv.getConfig().getAdmin();
			put("ansprechpartner", ans);
			boolean weiblich = false;
			try {
				weiblich = new PersonenService().getUser(ans).isWeiblich();
			} catch (Exception ignore) {
			}
			put("ansprechpartnerWeiblich", weiblich);
		}
		putNamensliste("bestelltHaben", "Bestellt haben", "Bestellt hat", woche.getBestellungen().stream()
				.filter(m -> !m.getBestellungen().isEmpty())
				.map(Mitarbeiterbestellung::getUser));
		putNamensliste("absagen", "Absagen von", "Absage von", woche.getNichtBestellen().stream());
		putNamensliste("keineAussageVon", "Noch keine Aussagen von", "Noch keine Aussage von", keineAussageVon(woche));
		return status;
	}

	private void putNamensliste(String var, String textPlural, String textSingular, Stream<String> namen) {
		String c = namen.sorted().collect(Collectors.joining(", "));
		if (!c.isEmpty()) {
			String text = c.contains(",") ? textPlural : textSingular;
			put(var, "<div>" + esc(text + ": " + c) + "</div>");
		}
	}
	
	private Stream<String> keineAussageVon(Woche woche) {
		List<String> tb = new PersonenService().getTypischeBesteller();
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

	private boolean isMontag(Woche woche) {
		return LocalDate.now().get(ChronoField.DAY_OF_WEEK) == DayOfWeek.MONDAY.getValue()
				&& (woche == null
					|| !DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()).equals(woche.getStartdatum()));
	}
	
	private void wasHeuteBestellt(Woche woche, Mitarbeiterbestellung mb) {
		final List<String> heuteBestellt = new ArrayList<>();
		final int today = LocalDate.now().get(ChronoField.DAY_OF_WEEK);
		for (Tag tag : woche.getTage()) {
			if (tag.getWochentag() == today) {
				for (Gericht gericht : tag.getGerichte()) {
					String id = gericht.getId();
					// Hat der User das bestellt?
					for (Gerichtbestellung gb : mb.getBestellungen()) {
						if (gb.getGerichtId().equals(id)) {
							heuteBestellt.add(gericht.getTitel());
						}
					}
				}
			}
		}
		put("heuteBestellt", esc(heuteBestellt.stream().collect(Collectors.joining(", "))));
		put("hasFuerHeuteBestellt", !heuteBestellt.isEmpty());
	}
}
