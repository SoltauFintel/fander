package de.mwvb.fander.rest;

import com.google.gson.Gson;

import de.mwvb.fander.model.Bestellung;
import de.mwvb.fander.model.User;
import de.mwvb.fander.service.FanderService;

/**
 * Gerichte bestellen
 * 
 * @see BestellungRequestJSON
 */
public class BestellenREST extends UnsereKarteREST {
    
    @Override
    protected UnsereKarteJSON work(User user) {
        FanderService sv = new FanderService();
        BestellungRequestJSON request = new Gson().fromJson(req.body(), BestellungRequestJSON.class);
        Bestellung bestellung = new Bestellung() {
            @Override
            public boolean isBestellt(String gerichtId) {
                return request.getGerichte().contains(gerichtId);
            }
        };
                
        sv.bestellen(bestellung, request.getLimit(), sv.byStartdatum(req), user.getUser());
        
        return super.work(user);
    }
}
