package de.mwvb.fander.rest;

import java.util.ArrayList;
import java.util.List;

public class UnsereKarteJSON {
    private String startdatum;
    private String ansprechpartner;
    private boolean geschlossen;
    private String user;
    private String limit;
    private boolean moechteNichtsBestellen = false;
    private final List<TagJSON> tage = new ArrayList<>();

    public String getStartdatum() {
        return startdatum;
    }

    public void setStartdatum(String startdatum) {
        this.startdatum = startdatum;
    }

    public String getAnsprechpartner() {
        return ansprechpartner;
    }

    public void setAnsprechpartner(String ansprechpartner) {
        this.ansprechpartner = ansprechpartner;
    }

    public boolean isGeschlossen() {
        return geschlossen;
    }

    public void setGeschlossen(boolean geschlossen) {
        this.geschlossen = geschlossen;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public boolean isMoechteNichtsBestellen() {
        return moechteNichtsBestellen;
    }

    public void setMoechteNichtsBestellen(boolean moechteNichtsBestellen) {
        this.moechteNichtsBestellen = moechteNichtsBestellen;
    }

    public List<TagJSON> getTage() {
        return tage;
    }
}
