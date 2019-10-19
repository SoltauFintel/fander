package de.mwvb.fander.actions;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.service.FanderService;

public class FanderConfigSaveAction extends SActionBase {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		FanderConfig c = sv.getConfig();
		
		c.setAdmin(req.queryParams("admin")); // Ansprechpartner
		c.setUrl(req.queryParams("url"));
		c.setMindestbestellbetrag(parseDouble(req.queryParams("mindestbestellbetrag")));
		c.setMindestbestellmenge(parseInt(req.queryParams("mindestbestellmenge")));
		String demo = req.queryParams("demomodus");
		System.out.println("demomodus: " + demo);
		c.setDemomodus("1".equals(demo));
		
		sv.save(c);
		info("FanderConfig gespeichert.");
		
		res.redirect("/");
	}

	private int parseInt(String a) {
		try {
			return Integer.parseInt(a);
		} catch (Exception e) {
			return 0;
		}
	}

	private double parseDouble(String a) {
		try {
			return Double.parseDouble(a.replace(",", "."));
		} catch (Exception e) {
			return 0d;
		}
	}
}
