package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class Wochen extends SAction {

	@Override
	protected void execute() {
		setTitle("Liste aller Fander Wochen");
		FanderService sv = new FanderService();
		if (!isAdmin() && !sv.getConfig().getAdmin().equalsIgnoreCase(user())) {
			throw new AuthException();
		}

		Woche j = sv.getJuengsteWoche();
		String j_id = j == null ? "" : j.getId();

		DataList list = list("wochen");
		for (Woche woche : sv.list()) {
			DataMap map = list.add();
			map.put("startdatum", woche.getStartdatum());
			map.put("startdatumNice", woche.getStartdatumNice());
			map.put("id", woche.getId());
			map.put("archiviert", woche.isArchiviert());
			map.put("bestellt", woche.isBestellt());
			map.put("bestellungenErlaubt", woche.isBestellungenErlaubt() && !woche.isArchiviert());
			map.put("juengsteWoche", woche.getId().equals(j_id));
		}
	}
}
