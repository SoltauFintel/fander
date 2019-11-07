package de.mwvb.fander.rest;

import com.google.gson.Gson;

import de.mwvb.fander.model.User;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

/**
 * Gerichte bestellen
 * 
 * @see BestellungRequestJSON
 */
public class BestellenREST extends UnsereKarteREST {
    // TODO Testcase
    
    @Override
    protected UnsereKarteJSON work(User user) {
        BestellungRequestJSON bestellung = new Gson().fromJson(req.body(), BestellungRequestJSON.class);
        
        FanderService sv = new FanderService();
        Woche woche = sv.byStartdatum(req);
        sv.bestellen(woche, user, bestellung);
        
        return super.work(user);
    }
}
