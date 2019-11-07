package de.mwvb.fander.rest;

public class MessageJSON {
    private String text;

    public MessageJSON(String text) {
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
