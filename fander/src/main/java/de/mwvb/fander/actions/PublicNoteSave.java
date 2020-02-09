package de.mwvb.fander.actions;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.DateService;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.User;

public class PublicNoteSave extends SActionBase {

    @Override
    protected void execute() {
        String user = user();
        info("PublicNoteSave " + user);
        String publicNote = req.queryParams("publicNote").trim();
        
        UserService sv = new UserService();
        User theUser = sv.getUser(user);
        theUser.setPublicNote(publicNote);
        theUser.setPublicNoteTimestamp(DateService.now());
        sv.save(theUser);
        
        res.redirect("/");
    }
}
