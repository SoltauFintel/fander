package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;

public class FanderMailAction extends SAction {

	@Override
	protected void execute() {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}
		info("FanderMailAction");

		FanderService sv = new FanderService();
		Woche woche = sv.byStartdatum(req);

		put("h1title", "'Bestellungen nun möglich' Infomails versenden");
		put("startdatum", esc(woche.getStartdatumNice()));
		
		DataList list = model.list("personen");
		for (MailEmpfaenger p : new UserService().getMailEmpfaenger()) {
			DataMap map = list.add();
			map.put("id", esc(p.getId()));
			map.put("name", esc(p.getName()));
			map.put("an", p.isAusgewaehlt());
		}
	}
}
