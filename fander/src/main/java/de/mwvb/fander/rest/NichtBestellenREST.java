package de.mwvb.fander.rest;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.model.User;
import de.mwvb.fander.service.FanderService;

/**
 * Ich m�chte nicht bestellen.
 */
public class NichtBestellenREST extends UserJsonAction<MessageJSON> {

    @Override
    protected MessageJSON work(User user) {
        boolean undo = "1".equals(req.queryParams("undo"));
        
        new FanderService().nichtBestellen(req, user.getUser(), undo);
        Logger.info("REST API: User: " + user.getUser() + " m�chte " + (undo ? "doch" : "nicht") + " bestellen");
        
        return new MessageJSON("ok");
    }
}
