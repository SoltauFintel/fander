package de.mwvb.fander.model;

import java.util.ArrayList;
import java.util.List;

//Embedded
public class Tag {
	/** 1 = Montag, 5 = Freitag */
	private int wochentag;
	private List<Gericht> gerichte;

	public Tag() {
	}
	
	public Tag(int wochentag) {
		this.wochentag = wochentag;
		gerichte = new ArrayList<>();
	}

	public int getWochentag() {
		return wochentag;
	}
	
	public String getWochentagText() {
		switch (wochentag) {
		case 1: return "Montag";
		case 2: return "Dienstag";
		case 3: return "Mittwoch";
		case 4: return "Donnerstag";
		case 5: return "Freitag";
		default: throw new RuntimeException("Wochentag Nummer unbekannt: " + wochentag);
		}
	}

	public void setWochentag(int wochentag) {
		if (wochentag < 1 || wochentag > 5) {
			throw new RuntimeException("Wochentag Nummer muss zwischen 1 und 5 sein. 1 steht für Montag. 5 für Freitag.");
		}
		this.wochentag = wochentag;
	}

	public List<Gericht> getGerichte() {
		return gerichte;
	}

	public void setGerichte(List<Gericht> gerichte) {
		this.gerichte = gerichte;
	}
	
	public void add(String gericht, String zusatzstoffe, double preis) {
		getGerichte().add(new Gericht(gericht, zusatzstoffe, preis));
	}

	/** Für Test */
	public void add(String gericht, double preis) {
		add(gericht, "?", preis);
	}

	public int getAnzahlGerichte() {
		return gerichte.size();
	}
	
	public boolean hasBestellteGerichte() {
		for (Gericht g : gerichte) {
			if (g.isBestellt()) {
				return true;
			}
		}
		return false;
	}
}
