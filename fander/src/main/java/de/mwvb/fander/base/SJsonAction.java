package de.mwvb.fander.base;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.rest.ErrorMessage;
import de.mwvb.maja.web.JsonAction;

public abstract class SJsonAction<JSON> extends JsonAction {

    @Override
    protected void execute() {
        try {
            result = work();
            if (result == null) {
                throw new RuntimeException("Keine JSON-Daten verfügbar!");
            }
        } catch (Exception e) {
            Logger.error(e);
            result = new ErrorMessage(e);
            res.status(500);
        }
    }
    
    protected abstract JSON work();
}
