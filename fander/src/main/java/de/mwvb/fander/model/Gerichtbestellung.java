package de.mwvb.fander.model;

// Embedded
public class Gerichtbestellung {
	private String gerichtId;

	public Gerichtbestellung() {
	}
	
	public Gerichtbestellung(String gerichtId) {
		this.gerichtId = gerichtId;
	}
	
	public String getGerichtId() {
		return gerichtId;
	}

	public void setGerichtId(String gerichtId) {
		this.gerichtId = gerichtId;
	}
}
