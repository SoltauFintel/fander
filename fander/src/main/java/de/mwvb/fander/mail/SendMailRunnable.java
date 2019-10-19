package de.mwvb.fander.mail;

import org.pmw.tinylog.Logger;

public class SendMailRunnable implements Runnable {
	private SendMailRequest mail;
	
	public SendMailRunnable(SendMailRequest mail) {
		this.mail = mail;
	}

	@Override
	public void run() {
		try {
			new MailSender().sendMail(mail);
			Logger.debug("Mail wurde gesendet. / Betreff: " + mail.getSubject() + " / to: " + mail.getToEmailaddress());
		} catch (Exception e) {
			e.printStackTrace(); // Ich lass das erstmal so.
		}
		mail = null;
	}
}
