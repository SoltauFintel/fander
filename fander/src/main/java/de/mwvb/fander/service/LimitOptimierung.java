package de.mwvb.fander.service;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.FanderBestellungGericht;
import de.mwvb.fander.model.FanderBestellungTag;
import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Woche;

/**
 * Min/Max-Optimierung für Fander Bestellung. Behandlung von mitarbeiterspezifischem Limit. Behandlung von Config-Tages-Mindestwerte.
 */
public class LimitOptimierung {
	public static boolean DEBUG = false;
	
	public void optimiereBestellung(Woche woche, FanderBestellung bestellung, FanderConfig config) {
		debug("Limit Optimierung");
		List<UserMitLimit> userliste = getAlleUserMitLimit(woche);
		for (UserMitLimit user : userliste) {
			String currentUser = user.getUser();
			int loop = 0;
			while (getAnzahlGerichte(currentUser, bestellung) > user.getLimit().intValue()) {
				loop++;
				if (loop > 3 * 7) throw new RuntimeException("Killerloop, " + currentUser);
				// Gehe all seine Bestellungen durch und finde die mit der größten Anzahl.
				FanderBestellungTag tag = findeFettestenTag(bestellung, currentUser, config);
				if (tag == null) {
					throw new RuntimeException("Limit Optimierung Fehler für User " + currentUser + ": keinen fettesten Tag gefunden");
				}
				// An diesem Tag nehme ich die Bestellung von user raus.
				boolean removed = false;
				for (FanderBestellungGericht g : tag.getGerichte()) {
					if (g.getBesteller().contains(currentUser)
							&& g.getAnzahl() > 0 /*sicherheitshalber*/) {
						debug("entferne Gericht \"" + g.getGericht() + "\" für User " + currentUser + ", Tag " + tag.getTag());
						g.dec(currentUser);
						removed = true;
						break;
					}
				}
				if (!removed) {
					throw new RuntimeException("Limit Optimierung Fehler für User " + currentUser + ": Gericht wurde nicht entfernt");
				}
			}
		}

		List<FanderBestellungTag> kill = new ArrayList<>();
		for (FanderBestellungTag ergebnisTag : bestellung.getTage()) {
			boolean rule1 = ergebnisTag.getAnzahl() >= config.getMindestbestellmenge() && ergebnisTag.getGesamtpreis() >= config.getMindestbestellbetrag();
			if (!rule1) {
				debug("Tag " + ergebnisTag.getTag() + " entfernt, weil Mindestwert (" + config.getMindestbestellmenge()
						+ "; " + config.getMindestbestellbetrag() + ") nicht erreicht." + " Anzahl: "
						+ ergebnisTag.getAnzahl() + ", Gesamtpreis: "
						+ FanderService.format(ergebnisTag.getGesamtpreis()));
				kill.add(ergebnisTag);
			}
		}
		bestellung.getTage().removeAll(kill);
	}
	
	private int getAnzahlGerichte(String user, FanderBestellung bestellung) {
		int ret = 0;
		for (FanderBestellungTag tag : bestellung.getTage()) {
			for (FanderBestellungGericht g : tag.getGerichte()) {
				if (g.getBesteller().contains(user)) {
					ret++;
				}
			}
		}
		return ret;
	}
	
	// Wer hat ein Limit?
	private List<UserMitLimit> getAlleUserMitLimit(Woche woche) {
		List<UserMitLimit> userMitLimit = new ArrayList<>();
		for (Mitarbeiterbestellung mb : woche.getBestellungen()) {
			if (mb.getLimit() != null && mb.getLimit().intValue() < mb.getBestellungen().size() && mb.getBestellungen().size() > 0) {
				userMitLimit.add(new UserMitLimit(mb.getUser(), mb.getLimit()));
			}
		}
		// Der mit dem kleinsten Limit zuerst. (bin aber nicht sicher ob das so richtig ist)
		userMitLimit.sort((a, b) -> a.getLimit().compareTo(b.getLimit()));
		return userMitLimit;
	}
	
	private static class UserMitLimit {
		private final String user;
		private final Integer limit;
		
		public UserMitLimit(String user, Integer limit) {
			this.user = user;
			this.limit = limit;
		}

		public String getUser() {
			return user;
		}

		public Integer getLimit() {
			return limit;
		}
	}

	public FanderBestellungTag findeFettestenTag(FanderBestellung bestellung, String user, FanderConfig config) {
		int max = 0;
		FanderBestellungTag fettesterTag = null;
		for (FanderBestellungTag tag : bestellung.getTage()) {
			if (hatUserAnDiesemTagBestellt(user, tag)) {
				int n = tag.getGerichte().size();
				if (tag.getAnzahl() < config.getMindestbestellmenge()) {
					return tag;
				}
				if (n > max) {
					max = n;
					fettesterTag = tag;
				}
			}
		}
		return fettesterTag;
	}

	private boolean hatUserAnDiesemTagBestellt(String user, FanderBestellungTag tag) {
		for (FanderBestellungGericht g : tag.getGerichte()) {
			if (g.getBesteller().contains(user)) {
				return true;
			}
		}
		return false;
	}
	
	protected void debug(String msg) {
		if (DEBUG) {
			System.out.println("...DEBUG>> " + msg);
		}
	}
}
