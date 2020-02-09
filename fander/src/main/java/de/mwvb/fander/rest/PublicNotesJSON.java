package de.mwvb.fander.rest;

import java.util.List;

public class PublicNotesJSON {
    private List<PublicNoteJSON> publicNotes;

    public List<PublicNoteJSON> getPublicNotes() {
        return publicNotes;
    }

    public void setPublicNotes(List<PublicNoteJSON> publicNotes) {
        this.publicNotes = publicNotes;
    }
}
