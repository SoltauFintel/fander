package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;
import de.mwvb.fander.service.PersonenService;

public class FanderMailAction extends SAction {

	@Override
	protected void execute() {
		FanderService sv = new FanderService();
		Woche woche = sv.byStartdatum(req);

		info("FanderMailAction");
		put("h1title", "'Bestellungen nun möglich' Infomails versenden");
		put("startdatum", esc(woche.getStartdatumNice()));
		
		// TODO Auswahl vom vorigen Mal vorschlagen -> Idee: bestimmten Kommentar eingeben und dann wird kein Mail versendet, sondern die Auswahl als Standard gespeichert.
		DataList list = model.list("personen");
		for (MailEmpfaenger p : new PersonenService().getMailEmpfaenger()) {
			DataMap map = list.add();
			map.put("id", p.getId());
			map.put("name", p.getName());
			map.put("an", p.isAusgewaehlt());
		}
	}
	
	@Override
	public String getPage() {
		return "fandermailaction";
	}
}
