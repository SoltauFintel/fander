package de.mwvb.fander.rest;

import org.pmw.tinylog.Logger;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SJsonAction;
import de.mwvb.fander.model.User;

public abstract class UserJsonAction<JSON> extends SJsonAction<JSON> {
    
    @Override
    protected final JSON work() {
        User user = getUser();
        Logger.info(getClass().getSimpleName() + ": " + user.getUser());
        return work(user);
    }
    
    /**
     * @param user nie null
     * @return response
     */
    protected abstract JSON work(User user);
    
    private User getUser() {
        String token = req.queryParams("ut");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("U1: User Token fehlt!");
        }
        User user = new UserService().byToken(token);
        if (user == null) {
            throw new RuntimeException("U2: User Token nicht bekannt oder gesperrt!");
        }
        return user;
    }
}
