package de.mwvb.fander.model;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class FanderBestellungGericht {
	private String gerichtId;
	private String gericht;
	private int anzahl = 0;
	private double einzelpreis = 0;
	private final Set<String> besteller = new TreeSet<>();
	private FanderBestellungTag tag;
	
	public String getGerichtId() {
		return gerichtId;
	}

	public void setGerichtId(String gerichtId) {
		this.gerichtId = gerichtId;
	}

	public String getGericht() {
		return gericht;
	}

	public void setGericht(String gericht) {
		this.gericht = gericht;
	}

	public int getAnzahl() {
		return anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	public void inc(String pBesteller) {
		anzahl++;
		besteller.add(pBesteller);
	}
	
	public void dec(String user) {
		anzahl--;
		besteller.remove(user);
	}

	public double getGesamtpreis() {
		return anzahl * einzelpreis;
	}

	public double getEinzelpreis() {
		return einzelpreis;
	}

	public void setEinzelpreis(double einzelpreis) {
		this.einzelpreis = einzelpreis;
	}

	public Set<String> getBesteller() {
		return besteller;
	}
	
	public String getBestellerText() {
		return getBesteller().stream().collect(Collectors.joining(", "));
	}

	public FanderBestellungTag getTag() {
		return tag;
	}

	public void setTag(FanderBestellungTag tag) {
		this.tag = tag;
	}
}
