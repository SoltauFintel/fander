package de.mwvb.fander.service;

import org.junit.Assert;
import org.junit.Test;

import de.mwvb.fander.model.User;

public class PersonenServiceTest {

	@Test
	public void list() {
		PersonenService psv = new PersonenService();
		for (User user : psv.getUsers()) {
			String line = user.isWeiblich() ? "W " : "  ";
			line += user.isInfomail() ? "a " : "  ";
			line += user.isTypischerBesteller() ? "t " : "  ";
			line += user.isZusatzstoffeAnzeigen() ? "z " : "  ";
			line += user.getNachname() + ", " + user.getVorname() + " <" + user.getEmailadresse() + "@...>";
			System.out.println(line);
		}
	}
	
	@Test
	public void hash() {
		long z = System.currentTimeMillis();
		String hash = User.hash("blob");
		System.out.println(hash);
		z = System.currentTimeMillis() - z;
		Assert.assertTrue("Passwortchiffrierung dauert zu lange: " + z + "ms", z < 100);
	}
}
