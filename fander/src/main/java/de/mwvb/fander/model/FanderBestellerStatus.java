package de.mwvb.fander.model;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.fander.service.FanderService;

// TODO prüfen, ob noch alle Getter benötigt werden
public class FanderBestellerStatus {
	private String user;
	private final List<Gericht> bestellteGerichte = new ArrayList<>();

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Gericht> getBestellteGerichte() {
		return bestellteGerichte;
	}

	public double getGesamtpreis() {
		return bestellteGerichte.stream().mapToDouble(Gericht::getPreis).sum();
	}
	
	public String getGesamtpreisFormatiert() {
		return FanderService.format(getGesamtpreis());
	}
}
