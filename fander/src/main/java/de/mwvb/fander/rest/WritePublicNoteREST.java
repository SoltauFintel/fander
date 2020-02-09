package de.mwvb.fander.rest;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.DateService;
import de.mwvb.fander.model.User;

public class WritePublicNoteREST extends UserJsonAction<MessageJSON> {

    @Override
    protected MessageJSON work(User user) {
        String text = req.queryParams("text");
        if (text == null) {
            throw new RuntimeException("Parameter 'text' fehlt!");
        }
        user.setPublicNote(text.trim());
        user.setPublicNoteTimestamp(DateService.now());
        new UserService().save(user);
        return new MessageJSON("ok");
    }
}
