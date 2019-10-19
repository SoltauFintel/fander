package de.mwvb.fander.service;

import java.util.stream.Stream;

import de.mwvb.fander.FanderApp;
import de.mwvb.fander.mail.MailSender;
import de.mwvb.fander.mail.SendMailRequest;
import de.mwvb.fander.model.MailEmpfaenger;

public class FanderMailService {

	/**
	 * @param empfaengerliste Liste der User-Namen, die ein Mail erhalten sollen
	 * @param kommentar immer getrimmt, nie null
	 * @param user angemeldeter User, normalerweise der Fander-Admin
	 */
	public void sendMail(Stream<MailEmpfaenger> empfaengerliste, String kommentar, String user) {
		PersonenService psv = new PersonenService();
		final String userL = psv.macheLang(user);
		final String mailtextTemplate = getMailtextTemplate(kommentar, user);
		empfaengerliste.forEach(p -> {
			String to = psv.getEmailadresse(p.getName());
			if (to != null) {
				SendMailRequest mail = new SendMailRequest();
				mail.setBody(mailtextTemplate.replace("{name}", p.getName()));
				mail.setSendername(userL + " (Fander App)");
				mail.setSubject("Fander");
				mail.setToEmailaddress(to);
				mail.setToName(p.getName());
				new MailSender().sendMail(mail);
			}
		});
	}

	private String getMailtextTemplate(String kommentar, String user) {
		String text = "Hallo {name}\r\n\r\n"
				+ "Fander Bestellungen sind ab sofort möglich!"
				+ "\r\nBitte hier bestellen: " + FanderApp.getHost() + "/bestellen";
		if (!kommentar.isEmpty()) {
			text += "\r\n\r\n" + kommentar;
		}
		return text + "\r\n\r\nGruß\r\n" + user + "\r\n" + FanderApp.getHost();
	}
}
