package de.mwvb.fander.auth;

import de.mwvb.fander.base.UserMessage;

public class AuthException extends UserMessage {

	public AuthException() {
		super("Du bist leider nicht berechtigt diese Seite aufzurufen!");
	}
}
