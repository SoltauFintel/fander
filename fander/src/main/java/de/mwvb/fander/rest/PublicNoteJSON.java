package de.mwvb.fander.rest;

public class PublicNoteJSON {
    private String user;
    private String publicNote;
    /** write action ignores this */
    private String publicNoteTimestamp;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPublicNote() {
        return publicNote;
    }

    public void setPublicNote(String publicNote) {
        this.publicNote = publicNote;
    }

    public String getPublicNoteTimestamp() {
        return publicNoteTimestamp;
    }

    public void setPublicNoteTimestamp(String publicNoteTimestamp) {
        this.publicNoteTimestamp = publicNoteTimestamp;
    }
}
