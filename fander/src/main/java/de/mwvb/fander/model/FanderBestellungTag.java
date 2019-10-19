package de.mwvb.fander.model;

import java.util.ArrayList;
import java.util.List;

public class FanderBestellungTag {
	private String tag;
	private List<FanderBestellungGericht> gerichte = new ArrayList<>();

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<FanderBestellungGericht> getGerichte() {
		return gerichte;
	}

	public void setGerichte(List<FanderBestellungGericht> gerichte) {
		this.gerichte = gerichte;
	}
	
	public int getAnzahl() {
		return gerichte.stream().mapToInt(FanderBestellungGericht::getAnzahl).sum();
	}

	public double getGesamtpreis() {
		return gerichte.stream().mapToDouble(FanderBestellungGericht::getGesamtpreis).sum();
	}
}
