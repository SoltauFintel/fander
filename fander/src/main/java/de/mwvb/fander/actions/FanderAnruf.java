package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.FanderBestellungGericht;
import de.mwvb.fander.model.FanderBestellungTag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

/*Weitere Features, die denkbar wären:

a) montags um einer gewissen Uhrzeit (welche?) das Menü laden

b) Wochenstart-Emails an Fander-Fans verschicken, wenn neues Menü geladen worden ist. Die Empfänger können das Mail natürlich abbestellen.

c) Emails an Besteller verschicken, wenn Bestellung geschlossen worden ist und Du die Freigabe für den Mailversand gegeben hast. Die Empfänger können das Mail natürlich abbestellen.
*/
public class FanderAnruf extends SAction {
	protected FanderService sv;
	protected Woche woche;
	
	@Override
	protected void execute() {
		setTitle("Fander Anrufseite");
		sv = new FanderService();
		check(sv);

		woche = sv.byStartdatum(req);
		put("startdatum", woche.getStartdatum());
		put("startdatumNice", woche.getStartdatumNice());
		put("bestellungenErlaubt", woche.isBestellungenErlaubt());
		put("bestellt", woche.isBestellt());
		
		FanderBestellung bestellung = sv.getFanderBestellung(woche, isVollstaendig());
		put("gesamtpreis", bestellung.getGesamtpreisFormatiert());
		put("gesamtanzahl", "" + bestellung.getGesamtanzahl());
		put("hatBestelldaten", !bestellung.getTage().isEmpty());
		
		DataList tage = list("tage");
		for (FanderBestellungTag tag : bestellung.getTage()) {
			DataMap map = tage.add();
			map.put("anzahl", "" + tag.getAnzahl());
			map.put("tag", tag.getTag());
			DataList gerichte = map.list("gerichte");
			for (FanderBestellungGericht g : tag.getGerichte()) {
				DataMap map2 = gerichte.add();
				map2.put("gericht", g.getGericht());
				map2.put("anzahl", "" + g.getAnzahl());
				map2.put("einzelpreis", FanderService.format(g.getEinzelpreis()));
				map2.put("gesamtpreis", FanderService.format(g.getGesamtpreis()));
				map2.put("namen", g.getBestellerText());
			}
		}
	}

	protected boolean isVollstaendig() {
		info("Fander Anrufseite");
		return false;
	}

	protected void check(FanderService sv) {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}
	}

	@Override
	public String getPage() {
		return "anruf";
	}
}
