package de.mwvb.fander.actions;

import java.util.List;
import java.util.stream.Collectors;

import de.mwvb.fander.auth.AuthException;
import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.base.SActionBase;
import de.mwvb.fander.base.UserMessage;
import de.mwvb.fander.mail.FanderMailService;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.maja.web.AppConfig;

public class FanderMailSendAction extends SActionBase {

	@Override
	protected void execute() {
		if (!isAnsprechpartner()) {
			throw new AuthException();
		}

		String mailerUrl = new AppConfig().get("mailer.url");
		if (mailerUrl == null || mailerUrl.trim().isEmpty()) {
			throw new UserMessage("Mail-Feature nicht konfiguriert!");
		}

		List<MailEmpfaenger> pl = new UserService().getMailEmpfaenger();
		pl.forEach(p -> p.setAusgewaehlt("1".equals(req.queryParams("c_" + p.getId()))));
		String kommentar = req.queryParams("kommentar").trim();
		
		info("FanderMailSendAction Kommentar: " + kommentar
				+ "\nEmpfängerliste: " + pl.stream().filter(MailEmpfaenger::isAusgewaehlt).map(MailEmpfaenger::getName).collect(Collectors.joining(", ")) + ".");
		
		new FanderMailService().sendMail(
				pl.stream().filter(MailEmpfaenger::isAusgewaehlt),
				kommentar, // Der Fander-Admin kann einen Text eingeben, der mit ins Mail kommt.
				user()); // Normalerweise der Fander-Admin.
		
		if (pl.stream().filter(MailEmpfaenger::isAusgewaehlt).count() > 0) {
			res.redirect("/infomails-sent"); // kleine Bestätigungsseite ausgeben
		} else {
			res.redirect("/");
		}
	}
}
