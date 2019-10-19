package de.mwvb.fander.service;

import org.junit.Test;

import de.mwvb.fander.model.KPerson;

public class PersonenServiceTest {

	@Test
	public void list() {
		PersonenService psv = new PersonenService();
		for (KPerson p : psv.getKPersonen()) {
			String line = p.isWeiblich() ? "W " : "  ";
			line += p.isAusgewaehlt() ? "a " : "  ";
			line += p.isTypischerBesteller() ? "t " : "  ";
			line += p.isZusatzstoffeAnzeigen() ? "z " : "  ";
			line += p.getNachname() + ", " + p.getVorname() + " <" + p.getEmailadresse() + "@...>";
			System.out.println(line);
		}
	}
}
