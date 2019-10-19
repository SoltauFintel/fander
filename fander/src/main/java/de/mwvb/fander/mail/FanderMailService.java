package de.mwvb.fander.service;

import java.util.stream.Stream;

import de.mwvb.fander.mail.MailSender;
import de.mwvb.fander.mail.SendMailRequest;
import de.mwvb.fander.model.MailEmpfaenger;
import de.mwvb.maja.web.AppConfig;

public class FanderMailService {

	/**
	 * @param empfaengerliste Liste der User-Namen, die ein Mail erhalten sollen
	 * @param kommentar immer getrimmt, nie null
	 * @param executingUser angemeldeter User, normalerweise der Fander-Admin
	 */
	public void sendMail(Stream<MailEmpfaenger> empfaengerliste, String kommentar, String executingUser) {
		UserService psv = new UserService();
		final String userL = psv.macheLang(executingUser);
		final String mailtextTemplate = getMailtextTemplate(kommentar, executingUser);
		empfaengerliste.forEach(empfaenger -> {
			SendMailRequest mail = new SendMailRequest();
			mail.setBody(mailtextTemplate.replace("{name}", empfaenger.getName()));
			mail.setSendername(userL + " (Fander App)");
			mail.setSubject("Fander");
			mail.setToEmailaddress(empfaenger.getEmailadresse());
			mail.setToName(empfaenger.getName());
			new MailSender().sendMail(mail);
		});
	}

	private String getMailtextTemplate(String kommentar, String user) {
		String host = new AppConfig().get("host");
		String text = "Hallo {name}\r\n\r\n"
				+ "Fander Bestellungen sind ab sofort möglich!"
				+ "\r\nBitte hier bestellen: " + host + "/bestellen";
		if (!kommentar.isEmpty()) {
			text += "\r\n\r\n" + kommentar;
		}
		return text + "\r\n\r\nGruß\r\n" + user + "\r\n" + host;
	}
}
