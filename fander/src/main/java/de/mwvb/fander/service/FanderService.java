package de.mwvb.fander.service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.base.DateService;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.dao.FanderConfigDAO;
import de.mwvb.fander.dao.WocheDAO;
import de.mwvb.fander.model.Bestellung;
import de.mwvb.fander.model.FanderBestellerStatus;
import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.FanderBestellungGericht;
import de.mwvb.fander.model.FanderBestellungTag;
import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Gerichtbestellung;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.maja.mongo.AbstractDAO;
import spark.Request;

// TODO Mail an alle Besteller, wenn geschlossen (auch f�r die, die nicht erfolgreich bestellt haben. Nicht aber f�r die Absager.)
/* weitere TODO s:
    - Mail senden wenn Bestellung geschlossen, Inhalt: Link auf Unsere Karte. An alle die bestellt haben (auch die nicht erfolgreich bestellt haben).
	- evtl. Mail senden wenn neue Woche gestartet, blo� an wen?
	- in der Mail Control sollte jeder das Fander Mail deaktivieren k�nnen. Ein verz�gertes Senden wird nicht eingebaut.
	- dr�ber nachdenken: tageweise schlie�en (oder Info Feld)
 */
public class FanderService {
	private final WocheDAO dao = new WocheDAO();
	
	public List<Woche> list() {
		return dao.list();
	}
	
	public Woche getJuengsteWoche() {
		Woche woche = dao.getJuengsteWoche();
		if (woche == null) {
			throw new KeineWocheException();
		}
		return woche;
	}

	public Woche byStartdatum(Request req) {
		String startdatum = req.params("startdatum");
		Woche woche = byStartdatum(startdatum);
		if (woche == null) {
			throw new KeineWocheException();
		} else if (woche.isArchiviert()) {
			throw new UserMessage("Woche " + startdatum + " ist archiviert und kann nicht mehr ver�ndert werden!");
		}
		return woche;
	}

	public String getJuengsteWocheId() {
		Woche woche = dao.getJuengsteWoche();
		if (woche == null) {
			return "";
		}
		return woche.getId();
	}

	public Woche byStartdatum(String startdatum) {
		return dao.byStartdatum(startdatum);
	}

	public void save(Woche woche) {
		removeLeereBestellungen(woche);
		dao.save(woche);
	}

	private void removeLeereBestellungen(Woche woche) {
		if (woche.getBestellungen() != null) {
			List<Mitarbeiterbestellung> kill = new ArrayList<>();
			for (Mitarbeiterbestellung mb : woche.getBestellungen()) {
				if (mb.getBestellungen() == null || mb.getBestellungen().isEmpty()) {
					kill.add(mb);
				}
			}
			woche.getBestellungen().removeAll(kill);
		}
	}

	public boolean delete(String startdatum) {
		Woche woche = byStartdatum(startdatum);
		if (woche != null) {
			dao.delete(woche);
			return true;
		}
		return false;
	}
	
	public FanderConfig getConfig() {
		FanderConfigDAO dao2 = new FanderConfigDAO();
		FanderConfig config = dao2.get(FanderConfig.ID);
		if (config == null) {
			config = new FanderConfig();
			dao2.save(config);
		}
		return config;
	}
	
	public void save(FanderConfig config) {
		new FanderConfigDAO().save(config);
	}

	// Story 1: neue Woche starten, Men� laden
	public Woche createNeueWoche(boolean mitPruefung) {
	    return createNeueWoche(getNeueWocheStartdatum(), getMenuLoader(), getConfig().getUrl(), mitPruefung);
	}
	
	public Woche createNeueWocheForTest(String startdatum) {
        Woche woche = createNeueWoche(startdatum, new FanderMenuLoader() {
            @Override
            public List<Tag> loadMenu(String url) {
                return parseMenu(getClass().getResourceAsStream(FanderMenuLoader.DEMO_DATEI));
            }
        }, null, false);
        save(woche);
        return woche;
	}

    public Woche createNeueWoche(String startdatum, FanderMenuLoader menuLoader, String url, boolean mitPruefung) {
        Woche woche = new Woche();
        woche.setId(AbstractDAO.code6(AbstractDAO.genId()));
        woche.setStartdatum(startdatum);
        woche.setTage(menuLoader.loadMenu(url));
        if (mitPruefung && (woche.getTage() == null || woche.getTage().isEmpty())) {
            throw new UserMessage("Es konnte keine neue Woche gestartet werden, da das Men� leer ist!"
                    + " M�glicherweise ist die Speisekarte noch nicht verf�gbar. Bitte versuche es sp�ter noch einmal.");
        }
        return woche;
    }

	public String getNeueWocheStartdatum() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
	}
	
	protected FanderMenuLoader getMenuLoader() {
		if (getConfig().isDemomodus()) {
			return new FanderMenuLoader() {
				@Override
				public List<Tag> loadMenu(String url) {
					Logger.debug("Fander Demomodus");
					return parseMenu(getClass().getResourceAsStream(FanderMenuLoader.DEMO_DATEI));
				}
			};
		} else {
			return new FanderMenuLoader();
		}
	}

	public FanderBestellung getFanderBestellung(Woche woche) {
		return getFanderBestellung(woche, false);
	}

	/**
	 * Story 4: Daten f�r Anruf bei Fander zusammenstellen
	 * <p>Wichtige Fachlichkeit
	 * <p>�brigens: Die finalen Bestelldaten werden nicht gespeichert. Durch �nderung der FanderConfig k�nnte
	 * sp�ter ein anderes Ergebnis raus kommen.
	 * 
	 * @param woche
	 * @param vollstaendig normalerweise false. Bei false wird die MinMax-Optimierung gemacht, was praxisechter ist.
	 * Mit true w�rde man sehen, wer was angekreuzt hat. Die Regeln sind dann aber nicht angewendet worden.
	 */
	public FanderBestellung getFanderBestellung(Woche woche, boolean vollstaendig) {
		FanderBestellung ret = new FanderBestellung();
		for (Tag tag : woche.getTage()) {
			FanderBestellungTag ergebnisTag = new FanderBestellungTag();

			addGerichte(woche, tag, ergebnisTag, vollstaendig);
			
			ergebnisTag.setTag(tag.getWochentagText());
			ret.getTage().add(ergebnisTag);
		}
		if (!vollstaendig) {
			new LimitOptimierung().optimiereBestellung(woche, ret, getConfig());
		}
		return ret;
	}
	
	private void addGerichte(Woche woche, Tag tag, FanderBestellungTag ergebnisTag, boolean vollstaendig) {
		for (Gericht g : tag.getGerichte()) {
			FanderBestellungGericht ergebnisGericht = new FanderBestellungGericht();
			ergebnisGericht.setGerichtId(g.getId());
			ergebnisGericht.setGericht(g.getTitel());
			ergebnisGericht.setEinzelpreis(g.getPreis());
			ergebnisGericht.setTag(ergebnisTag);
			
			// Mitarbeiterbestellungen suchen
			for (Mitarbeiterbestellung b : woche.getBestellungen()) {
				for (Gerichtbestellung gb : b.getBestellungen()) {
					if (gb.getGerichtId().equals(g.getId())) {
						ergebnisGericht.inc(b.getUser());
					}
				}
			}
			
			if (ergebnisGericht.getAnzahl() > 0 || vollstaendig) {
				ergebnisTag.getGerichte().add(ergebnisGericht);
			}
		}
	}
	
	public List<FanderBestellerStatus> getFanderBestellerStatus(Woche woche) {
		return getFanderBestellerStatus(woche, getFanderBestellung(woche));
	}
	
	/**
	 * Story 4: Was haben die User bestellt? Gesamtpreis pro User.
	 * <p>Wichtige Fachlichkeit
	 */
	public List<FanderBestellerStatus> getFanderBestellerStatus(Woche woche, FanderBestellung bestellung) {
		List<FanderBestellerStatus> statusAlle = new ArrayList<>();
		for (Mitarbeiterbestellung mb : woche.getBestellungen()) {
			FanderBestellerStatus status = new FanderBestellerStatus();
			status.setUser(mb.getUser());
			// Schnittmenge aus mb.bestellungen und bestellung bilden
			for (Gerichtbestellung gb : mb.getBestellungen()) {
				// Ist die Mitarbeiter-Gerichtbestellung auch in der finalen bestellung vorhanden?
				if (sucheMitarbeitergerichtbestellungInFinalerBestellung(mb.getUser(), gb.getGerichtId(), bestellung)) {
					status.getBestellteGerichte().add(getGericht(gb.getGerichtId(), woche));
				}
			}
			if (!status.getBestellteGerichte().isEmpty()) {
				statusAlle.add(status);
			}
		}
		statusAlle.sort((a, b) -> a.getUser().compareToIgnoreCase(b.getUser()));
		return statusAlle;
	}
	
	private boolean sucheMitarbeitergerichtbestellungInFinalerBestellung(String user, String gerichtId, FanderBestellung bestellung) {
		for (FanderBestellungTag tag : bestellung.getTage()) {
			for (FanderBestellungGericht g : tag.getGerichte()) {
				if (g.getBesteller().contains(user) && g.getGerichtId().equals(gerichtId)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Gericht getGericht(String gerichtId, Woche woche) {
		for (Tag tag : woche.getTage()) {
			for (Gericht gericht : tag.getGerichte()) {
				if (gericht.getId().equals(gerichtId)) {
					gericht.setWochentag(tag.getWochentagText());
					return gericht;
				}
			}
		}
		throw new RuntimeException("Gericht " + gerichtId + " nicht gefunden");
	}

    public void bestellen(Bestellung bestellung, Integer limit, Woche woche, String user) {
        if (!woche.isBestellungenErlaubt()) {
            throw new UserMessage("Woche " + woche.getStartdatum() + " darf nicht mehr ver�ndert werden!");
        }

        Mitarbeiterbestellung mb = getMitarbeiterbestellung(woche, user);
        Logger.info(mb.getSpeicherinfo());
        bestellteGerichteVerzeichnen(woche, mb, bestellung, limit);
        Logger.info(log(mb, woche)); // Bestellungen ausloggen, damit die im Falle eines Absturzes noch wenigstens in der Console bzw. im Log stehen.
        save(woche);
        int n = mb.getBestellungen().size();
        Logger.info("Mitarbeiter " + user + " hat " + n + " Gericht" + (n == 1 ? "" : "e") + " bestellt.");
    }

	public Mitarbeiterbestellung getMitarbeiterbestellung(Woche woche, String user) {
		Mitarbeiterbestellung mb = null;
		if (woche.getBestellungen() != null) {
			for (Mitarbeiterbestellung i : woche.getBestellungen()) {
				if (i.getUser().equals(user)) {
					mb = i;
					break;
				}
			}
		}
		if (mb == null) {
			mb = new Mitarbeiterbestellung();
			mb.setUser(user);
			mb.setSpeicherinfo("Neue Bestellung f�r " + user + ". Woche " + woche.getStartdatum());
			mb.setErstelltAm(DateService.now());
			woche.getBestellungen().add(mb);
		} else {
			mb.setSpeicherinfo("Bestellung f�r " + user + " wird fortgeschrieben. Woche " + woche.getStartdatum());
			mb.setBearbeitetAm(DateService.now());
		}
		return mb;
	}

	/** Bestellung speichern Zeitpunkt (also vor dem Speichern) */
	private void bestellteGerichteVerzeichnen(Woche woche, Mitarbeiterbestellung mb, Bestellung bestellung, Integer limit) {
		for (Tag tag : woche.getTage()) {
			for (Gericht g : tag.getGerichte()) {
				Gerichtbestellung gb = getGerichtbestellung(g, mb);
                boolean checked = bestellung.isBestellt(g.getId());
				if (checked && gb == null) {
					gb = new Gerichtbestellung();
					gb.setGerichtId(g.getId());
					mb.getBestellungen().add(gb);
				} else if (!checked && gb != null) {
					mb.getBestellungen().remove(gb);
				}
			}
		}
		if (!mb.getBestellungen().isEmpty()) {
		    woche.getNichtBestellen().remove(mb.getUser());
		}
		mb.setLimit(limit);
	}
	
    private String log(Mitarbeiterbestellung mb, Woche woche) {
        String a = "Bestellung f�r User " + mb.getUser() + " in Fander Woche " + woche.getStartdatum()
                + " abgesendet. Limit: " + (mb.getLimit() == null ? "kein" : mb.getLimit()) + ", "
                + mb.getBestellungen().size() + " Gericht(e):";
        for (Gerichtbestellung gb : mb.getBestellungen()) {
            Gericht gericht = getGericht(gb.getGerichtId(), woche);
            a += "\n- " + gericht.getTitel();
        }
        return a;
    }

	/** Bestellung anzeigen Zeitpunkt (also nach dem Speichern) */
	public void bestellteGerichteVerzeichnen(Woche woche, Mitarbeiterbestellung mb) {
		for (Tag tag : woche.getTage()) {
			for (Gericht g : tag.getGerichte()) {
				Gerichtbestellung gb = getGerichtbestellung(g, mb);
				g.setBestellt(gb != null);
			}
		}
	}

	private Gerichtbestellung getGerichtbestellung(Gericht g, Mitarbeiterbestellung mb) {
		for (Gerichtbestellung i : mb.getBestellungen()) {
			if (i.getGerichtId().equals(g.getId())) {
				return i;
			}
		}
		return null;
	}
	
	public void alteWochenArchivieren(Woche ausser) {
		if (ausser == null || ausser.getId() == null) {
			throw new RuntimeException("ausser nicht gesetzt oder ohne ID");
		}
		for (Woche woche : dao.list()) {
			if (!woche.getId().equals(ausser.getId()) && !woche.isArchiviert()) {
				woche.setArchiviert(true);
				save(woche);
			}
		}
	}
	
	public void nichtBestellen(Request req, String user, boolean undo) {
		Woche woche = byStartdatum(req);
		if (undo) { // doch bestellen
			woche.getNichtBestellen().remove(user);
		} else { // nicht bestellen
			woche.getNichtBestellen().add(user);
			for (Mitarbeiterbestellung mb : woche.getBestellungen()) {
				if (mb.getUser().equals(user)) {
					woche.getBestellungen().remove(mb);
					break;
				}
			}
		}
		save(woche);
	}
	
	public void angerufen(Request req, boolean bestellt) {
		Woche woche = byStartdatum(req);
		woche.setBestellt(bestellt);
		save(woche);
	}
	
	/** Formatiere Betrag auf 2 NK-Stellen */
	public static String format(double betrag) {
		return new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.GERMANY)).format(betrag);
		// Wegen JavaScript lieber kein Tausenderzeichen. Kommt in der Praxis eh nicht vor.
	}
}
