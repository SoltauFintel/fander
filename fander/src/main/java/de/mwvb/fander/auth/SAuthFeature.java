package de.mwvb.fander.auth;

import de.mwvb.maja.auth.AuthFeature;
import de.mwvb.maja.auth.AuthPlugin;
import de.mwvb.maja.web.AbstractWebApp;

public class SAuthFeature implements AuthFeature {
	private AuthPlugin authPlugin;
	
	@Override
	public void init(AuthPlugin owner) {
		this.authPlugin = owner;
	}

	@Override
	public void routes() {
		AbstractWebApp.POST("/login2", Login2.class);
		authPlugin.addNotProtected("/login");
	}
}
