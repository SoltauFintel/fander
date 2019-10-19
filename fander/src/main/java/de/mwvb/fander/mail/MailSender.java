package de.mwvb.fander.mail;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.pmw.tinylog.Logger;

import com.google.gson.Gson;

import de.mwvb.maja.web.AppConfig;

public class MailSender {
	private final AppConfig config = new AppConfig();
	
	public Exception sendMail(SendMailRequest mail) {
		try {
			sendMailR(mail);
			return null;
		} catch (Exception e) {
			Logger.error(e);
			return e;
		}
	}
	
	private void sendMailR(SendMailRequest mail) throws Exception {
		if (!"true".equalsIgnoreCase(config.get("send.mails"))) {
			System.out.println("DEMO MODUS. Send mail to: \"" + mail.getToName() + "\" <" + mail.getToEmailaddress() + ">");
			System.out.println("- SUBJECT: " + mail.getSubject());
			System.out.println(mail.getBody());
			System.out.println("=============================");
			return;
		}
		if (mail.getSendername() == null) {
			mail.setSendername("Fander App");
		}
		mail.setCode(config.get("mailer.code"));
		if (mail.getCode() == null) {
			throw new RuntimeException("mail.code fehlt in AppConfig.properties!");
		}
		String json = new Gson().toJson(mail);
		String url = config.get("mailer.url");
		CloseableHttpClient httpClient = HttpClients.custom().build();
		URI uri = URI.create(url);
		HttpPost post = new HttpPost(url);
		post.setEntity(new StringEntity(json));
		HttpResponse response = httpClient.execute(
				new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()),
				post,
				HttpClientContext.create());
		String r = EntityUtils.toString(response.getEntity());
		if (!"ok".equals(r)) {
			throw new RuntimeException("Send mail error: " + r);
		}
	}
}
