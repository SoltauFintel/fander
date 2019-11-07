package de.mwvb.fander.rest;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.model.User;

public class LogoutREST extends UserJsonAction<MessageJSON> {

    @Override
    protected MessageJSON work(User user) {
        new UserService().logout(user);
        return new MessageJSON("ok");
    }
}
