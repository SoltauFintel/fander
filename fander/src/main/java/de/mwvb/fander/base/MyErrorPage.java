package de.mwvb.fander.base;

import org.pmw.tinylog.Logger;

import de.mwvb.maja.web.ErrorPage;

public class MyErrorPage extends SAction implements ErrorPage {
	protected Exception exception;
	protected String msg;
	
	@Override
	public void setException(Exception exception) {
		this.exception = exception;
		if (exception.getMessage() == null || exception.getMessage().trim().isEmpty()) {
			msg = exception.getClass().getName();
		} else {
			msg = exception.getMessage();
			
			int o = msg.indexOf(SAction.CLASS); // Die Class-Info braucht der User nicht. Für mich steht die Class dann in der Console.
			if (o >= 0) {
				msg = msg.substring(0, o);
			}
		}
		msg = esc(msg);
	}

	@Override
	protected void execute() {
		Logger.error(exception);
		res.status(500);
		setTitle("Fehler");
		put("msg", msg.replace("\n", "<br/>"));
		put("anwendungstitel", "Fander App");
	}
}
