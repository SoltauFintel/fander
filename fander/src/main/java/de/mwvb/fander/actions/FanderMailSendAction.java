package de.mwvb.fander.actions;

import java.util.List;
import java.util.stream.Collectors;

import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.fander.service.FanderMailService;
import de.mwvb.fander.service.PersonenService;

public class FanderMailSendAction extends SActionBase {

	@Override
	protected void execute() {
		info("FanderMailSendAction");
		List<MailEmpfaenger> pl = new PersonenService().getPersonen();
		pl.forEach(p -> p.setAusgewaehlt("1".equals(req.queryParams("c_" + p.getId()))));
		String kommentar = req.queryParams("kommentar").trim();
		
		info("FanderMailSend Kommentar: " + kommentar
				+ "\nEmpfängerliste: " + pl.stream().filter(MailEmpfaenger::isAusgewaehlt).map(MailEmpfaenger::getName).collect(Collectors.joining(", ")) + ".");
		
		new FanderMailService().sendMail(
				pl.stream().filter(MailEmpfaenger::isAusgewaehlt),
				kommentar, // Der Fander-Admin kann einen Text eingeben, der mit ins Mail kommt.
				user()); // Normalerweise der Fander-Admin.
		
		if (pl.stream().filter(MailEmpfaenger::isAusgewaehlt).count() > 0) {
			res.redirect("/mail/sent"); // kleine Bestätigungsseite ausgeben
		} else {
			res.redirect("/");
		}
	}
}
