package de.mwvb.fander.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;

@Entity
@Indexes({ @Index(fields = { @Field("startdatum") }, options = @IndexOptions(unique = true)) })
public class Woche {
	@Id
	private String id;
	/** yyyy-mm-dd */
	private String startdatum;
	private boolean bestellungenErlaubt = true;
	private boolean archiviert = false;
	private boolean bestellt = false;
	private List<Tag> tage;
	private final List<Mitarbeiterbestellung> bestellungen = new ArrayList<>();
	private final Set<String> nichtBestellen = new TreeSet<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStartdatum() {
		return startdatum;
	}

	public String getStartdatumNice() {
		if (startdatum == null || startdatum.length() < "2018-12-12".length()) {
			return startdatum;
		}
		return startdatum.substring(8, 10) + "." + startdatum.substring(5, 7) + "." + startdatum.substring(0, 4)
				+ startdatum.substring(10);
	}

	public void setStartdatum(String startdatum) {
		this.startdatum = startdatum;
	}

	public boolean isBestellungenErlaubt() {
		return bestellungenErlaubt;
	}

	public void setBestellungenErlaubt(boolean bestellungenErlaubt) {
		this.bestellungenErlaubt = bestellungenErlaubt;
	}

	public boolean isArchiviert() {
		return archiviert;
	}

	public void setArchiviert(boolean archiviert) {
		this.archiviert = archiviert;
	}

	public List<Tag> getTage() {
		return tage;
	}

	public void setTage(List<Tag> tage) {
		this.tage = tage;
	}

	public List<Mitarbeiterbestellung> getBestellungen() {
		return bestellungen;
	}

	public int getAnzahlGerichte() {
		return tage.stream().mapToInt(Tag::getAnzahlGerichte).sum();
	}

	public boolean isBestellt() {
		return bestellt;
	}

	public void setBestellt(boolean bestellt) {
		this.bestellt = bestellt;
	}

	/**
	 * @return alle User, die explizit diese Woche nicht bei Fander bestellen möchten
	 */
	public Set<String> getNichtBestellen() {
		return nichtBestellen;
	}
}
