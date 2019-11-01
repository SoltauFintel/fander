package de.mwvb.fander.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

import org.mongodb.morphia.annotations.Transient;

import de.mwvb.fander.service.FanderService;
import de.mwvb.maja.mongo.AbstractDAO;

//Embedded
public class Gericht {
	private String id;
	private String titel;
	private double preis;
	private String zusatzstoffe = "?";
	@Transient
	private String wochentag;
	@Transient
	private boolean bestellt;
	@Transient
	private String namen;
	@Transient
	private boolean wirdBestellt;

	public Gericht() {
	}
	
	public Gericht(String titel, String zusatzstoffe, double preis) {
		this.id = AbstractDAO.genId();
		this.titel = titel;
		this.zusatzstoffe = zusatzstoffe;
		this.preis = preis;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitel() {
		return titel;
	}

	public void setTitel(String titel) {
		this.titel = titel;
	}

	public double getPreis() {
		return preis;
	}
	
	public String getPreisFormatiert() {
		return FanderService.format(preis);
		// Wegen JavaScript lieber kein Tausenderzeichen. Kommt in der Praxis eh nicht vor.
	}

	public void setPreis(double preis) {
		this.preis = preis;
	}

	public String getZusatzstoffe() {
		return zusatzstoffe;
	}

	public void setZusatzstoffe(String zusatzstoffe) {
		this.zusatzstoffe = zusatzstoffe;
	}

	public String getWochentag() {
		return wochentag;
	}

	public void setWochentag(String wochentag) {
		this.wochentag = wochentag;
	}
	
	public boolean isBestellt() {
		return bestellt;
	}

	public void setBestellt(boolean bestellt) {
		this.bestellt = bestellt;
	}

	public String getNamen() {
		return namen == null ? "" : namen;
	}

	public void setNamen(String namen) {
		this.namen = namen;
	}

	public boolean isWirdBestellt() {
		return wirdBestellt;
	}

	public void setWirdBestellt(boolean wirdBestellt) {
		this.wirdBestellt = wirdBestellt;
	}
	
	public boolean heuteBestellt(int tag) {
        if (isBestellt() && isWirdBestellt()) { // beide Abfragen notwendig!
            DayOfWeek heutigerWochentag = LocalDate.now().getDayOfWeek();
            return (tag == 1 && DayOfWeek.MONDAY.equals(heutigerWochentag))
                || (tag == 2 && DayOfWeek.TUESDAY.equals(heutigerWochentag))
                || (tag == 3 && DayOfWeek.WEDNESDAY.equals(heutigerWochentag))
                || (tag == 4 && DayOfWeek.THURSDAY.equals(heutigerWochentag))
                || (tag == 5 && DayOfWeek.FRIDAY.equals(heutigerWochentag))
                || (tag == 6 && DayOfWeek.SATURDAY.equals(heutigerWochentag))
                || (tag == 7 && DayOfWeek.SUNDAY.equals(heutigerWochentag));
        }
        return false;
	}

	@Override
	public String toString() {
		return getTitel() + " (" + getPreisFormatiert() + " EUR)";
	}
}
