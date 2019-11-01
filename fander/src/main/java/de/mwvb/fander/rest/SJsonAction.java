package de.mwvb.fander.rest;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.model.User;
import de.mwvb.maja.rest.ErrorMessage;
import de.mwvb.maja.web.JsonAction;

public abstract class SJsonAction<JSON> extends JsonAction {

    @Override
    protected void execute() {
        try {
            User user = getUser();
            Logger.info(getClass().getSimpleName() + ": " + user.getUser());
            result = work(user);
            if (result == null) {
                throw new RuntimeException("Keine JSON-Daten verfügbar!");
            }
        } catch (Exception e) {
            Logger.error(e);
            result = new ErrorMessage(e);
        }
    }
    
    private User getUser() {
        String token = req.queryParams("ut");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("User Token fehlt!");
        }
        User user = new UserService().byToken(token);
        if (user == null) {
            throw new RuntimeException("User Token nicht bekannt oder gesperrt!");
        }
        return user;
    }

    protected abstract JSON work(User user);
}
