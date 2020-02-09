package de.mwvb.fander.rest;

import java.util.ArrayList;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.model.PublicNoteEntry;
import de.mwvb.fander.model.User;

public class ReadPublicNoteREST extends UserJsonAction<PublicNotesJSON> {

    @Override
    protected PublicNotesJSON work(User user) {
        PublicNotesJSON ret = new PublicNotesJSON();
        ret.setPublicNotes(new ArrayList<>());
        if ("1".equals(req.queryParams("all"))) {
            UserService sv = new UserService();
            for (PublicNoteEntry e : sv.getPublicNotes()) {
                PublicNoteJSON note = new PublicNoteJSON();
                note.setUser(e.getUser());
                note.setPublicNote(e.getPublicNote());
                note.setPublicNoteTimestamp(e.getPublicNoteTimestamp());
                ret.getPublicNotes().add(note);
            }
        } else { // nur eigenen User liefern
            PublicNoteJSON note = new PublicNoteJSON();
            note.setUser(user.getUser());
            note.setPublicNote(user.getPublicNote());
            note.setPublicNoteTimestamp(user.getPublicNoteTimestamp());
            ret.getPublicNotes().add(note);
        }
        return ret;
    }
}
