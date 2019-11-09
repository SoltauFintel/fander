package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.Bestellung;
import de.mwvb.fander.service.FanderService;

public class BestellungAbsenden extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		sv.bestellen(getBestellung(), getLimit(), sv.byStartdatum(req), user());
		res.redirect("/");
	}

    private Bestellung getBestellung() {
        return new Bestellung() {
            @Override
            public boolean isBestellt(String gerichtId) {
                return req.queryParams("c_" + gerichtId) != null;
            }
        };
    }

	private Integer getLimit() {
		try {
			return Integer.valueOf(Integer.parseInt(req.queryParams("limit")));
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
