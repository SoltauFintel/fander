package de.mwvb.fander.rest;

import java.util.ArrayList;
import java.util.List;

import de.mwvb.fander.model.Gericht;

public class TagJSON {
    /** z.B. "Montag" */
    private String tag;
    /** 1-5 */
    private int tagNummer;
    /** Liste kann leer sein */
    private final List<Gericht> gerichte = new ArrayList<>();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getTagNummer() {
        return tagNummer;
    }

    public void setTagNummer(int tagNummer) {
        this.tagNummer = tagNummer;
    }

    public List<Gericht> getGerichte() {
        return gerichte;
    }
}
