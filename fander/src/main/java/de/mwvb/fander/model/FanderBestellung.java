package de.mwvb.fander.model;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.fander.service.FanderService;

/**
 * Bestelldaten für Telefonanruf bei Fander
 */
public class FanderBestellung {
	private List<FanderBestellungTag> tage = new ArrayList<>();

	public List<FanderBestellungTag> getTage() {
		return tage;
	}

	public void setTage(List<FanderBestellungTag> tage) {
		this.tage = tage;
	}
	
	public int getGesamtanzahl() {
		return tage.stream().mapToInt(FanderBestellungTag::getAnzahl).sum();
	}
	
	public boolean containsTag(String wochentagText) {
		for (FanderBestellungTag tag : tage) {
			if (tag.getTag().equals(wochentagText)) {
				return true;
			}
		}
		return false;
	}
	
	public double getGesamtpreis() {
		return tage.stream().mapToDouble(FanderBestellungTag::getGesamtpreis).sum();
	}

	public String getGesamtpreisFormatiert() {
		return FanderService.format(getGesamtpreis());
	}
}
