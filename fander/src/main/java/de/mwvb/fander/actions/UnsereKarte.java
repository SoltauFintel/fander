package de.mwvb.fander.actions;

import java.util.List;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.model.FanderBestellerStatus;
import de.mwvb.fander.service.FanderService;

public class UnsereKarte extends FanderAnruf {
	
	@Override
	protected void execute() {
		super.execute();
		bestellersummen();
	}

	private void bestellersummen() {
		List<FanderBestellerStatus> statusAlle = sv.getFanderBestellerStatus(woche);
		put("summenAnzeigen", isAnsprechpartner() && !statusAlle.isEmpty());
		DataList list = list("statusAlle");
		double summe = 0d;
		for (FanderBestellerStatus status : statusAlle) {
			DataMap map = list.add();
			map.put("user", status.getUser());
			map.put("gesamtpreis", status.getGesamtpreisFormatiert());
			summe += status.getGesamtpreis();
		}
		put("summe", FanderService.format(summe));
	}
	
	@Override
	protected void check(FanderService sv) {
		// Seite für jeden verfügbar
		setTitle("Unsere Karte");
	}
	
	@Override
	protected boolean isVollstaendig() {
		boolean ret = "1".equals(req.queryParams("vollstaendig"));
		put("mitVollstaendigeKarteLink", !ret);
		if (ret) {
			info("Unsere Karte, vollständig");
		} else {
			info("Unsere Karte");
		}
		return ret;
	}
	
	@Override
	public String getPage() { // Muss angegeben werden, da von FanderAnruf abgeleitet!
		return "unserekarte";
	}
}
