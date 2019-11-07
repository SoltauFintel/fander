package de.mwvb.fander.rest;

import de.mwvb.fander.model.User;
import de.mwvb.fander.startseite.StartseiteService;
import de.mwvb.fander.startseite.Zustand;

public class UnsereKarteREST extends UserJsonAction<UnsereKarteJSON> {

    @Override
    protected UnsereKarteJSON work(User user) {
        Zustand zustand = new StartseiteService().getZustand(user.getUser(), false);
        return zustand.getJSON();
    }
}
