package de.mwvb.fander.rest;

import java.util.List;

public class BestellungRequestJSON {
    /**
     * Wieviel Gerichte sollen maximal bestellt werden?
     * null: ohne Limit
     * 
     * Der Benutzer kann bspw. 3 Gerichte bestellen, setzt aber das Limit auf 2. Für ihn werden dann nur 2 Gerichte bestellt.
     * Der Fander App Algorithmus bestimmt dann, welche Gerichte er bekommt.
     */
    private Integer limit = null;
    /**
     * Gericht ID Liste
     * 
     * Hier sind alle Gerichte anzugeben, die der Benutzer für diese Woche bestellen möchte.
     * Wenn erst eine Bestellung mit Gericht A abgeschickt wird und danach eine weitere Bestellung mit Gericht B, ist nur Gericht B gespeichert,
     * da die Bestellung immer überschrieben wird.
     * 
     * Die bereits bestellten Gerichte erhält man übrigens über den Unsere Karte Aufruf, wobei bestellt=true gesetzt ist. Nicht zu verwechseln
     * mit wirdBestellt.
     */
    private List<String> gerichte;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<String> getGerichte() {
        return gerichte;
    }

    public void setGerichte(List<String> gerichte) {
        this.gerichte = gerichte;
    }
}
