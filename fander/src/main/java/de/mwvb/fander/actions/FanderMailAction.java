package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SAction;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;
import de.mwvb.maja.web.AppConfig;

public class FanderMailAction extends SAction {

	@Override
	protected void execute() {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}
		info("FanderMailAction");

		String mailerUrl = new AppConfig().get("mailer.url");
		if (mailerUrl == null || mailerUrl.trim().isEmpty()) {
			throw new UserMessage("Mail-Feature nicht konfiguriert!");
		}
		FanderService sv = new FanderService();
		Woche woche = sv.getJuengsteWoche();

		put("h1title", "'Bestellungen nun m�glich' Infomails versenden");
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
