package de.mwvb.fander.service;

import de.mwvb.fander.base.UserMessage;

public class KeineWocheException extends UserMessage {

	public KeineWocheException() {
		super("Keine Woche vorhanden!");
	}
}
