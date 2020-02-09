package de.mwvb.fander.actions;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.User;

public class PublicNote extends SAction {

    @Override
    protected void execute() {
        String user = user();
        info("PublicNote");
        User theUser = new UserService().getUser(user);
        setTitle("Öffentliche Notiz eingeben");
        put("publicNote", esc(theUser.getPublicNote()));
    }
}
