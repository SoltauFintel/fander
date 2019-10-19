package de.mwvb.fander;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.mwvb.fander.model.FanderBestellerStatus;
import de.mwvb.fander.model.FanderBestellung;
import de.mwvb.fander.model.FanderBestellungGericht;
import de.mwvb.fander.model.FanderBestellungTag;
import de.mwvb.fander.model.FanderConfig;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Gerichtbestellung;
import de.mwvb.fander.model.Mitarbeiterbestellung;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderMenuLoader;
import de.mwvb.fander.service.FanderService;
import de.mwvb.fander.service.LimitOptimierung;
import de.mwvb.maja.mongo.AbstractDAO;

public class FanderTest {
	// TODO Bestell-Limit Fachlichkeit einbauen
	
	@BeforeClass
	public static void open() {
		new FanderApp().startForTest();
	}

	@AfterClass
	public static void close() {
		AbstractDAO.database.close();
	}

	@Test
	public void neueWoche() {
		// Prepare
		System.out.println("\n\n--------------------------------------------------");
		FanderService sv = getService();
		
		// Test
		Woche woche = sv.createNeueWoche();
		
		// Verify
		Assert.assertNotNull(woche);
		Assert.assertFalse(woche.getTage().isEmpty());
		Assert.assertNotNull(woche.getStartdatum());
		Assert.assertTrue(woche.isBestellungenErlaubt());
		Assert.assertFalse(woche.isArchiviert());
		
		System.out.println("Woche " + woche.getStartdatum());
		for (Tag tag : woche.getTage()) {
			System.out.println("Tag: " + tag.getWochentag() + " - " + tag.getWochentagText());
			for (Gericht gericht : tag.getGerichte()) {
				System.out.println("\t\t- " + gericht.getTitel() + " (" + gericht.getPreisFormatiert() + " EUR)");
			}
		}
	}

	private FanderService getService() {
		return new FanderService() {
			@Override
			protected FanderMenuLoader getMenuLoader() {
				return new MenuLoader();
			}
			
			@Override
			public void save(Woche woche) {
				throw new RuntimeException("save(Woche) has been deactivated for testcase");
			}
		};
	}

	private FanderService getService3() {
		return new FanderService() {
			@Override
			protected FanderMenuLoader getMenuLoader() {
				return new MenuLoader();
			}
			
			@Override
			public void save(Woche woche) {
				throw new RuntimeException("save(Woche) has been deactivated for testcase");
			}
			
			@Override
			public FanderConfig getConfig() {
				FanderConfig c = super.getConfig();
				c.setMindestbestellmenge(3);
				return c;
			}
		};
	}

	@Test
	public void mehrereUserBestellen() {
		// Prepare
		FanderService sv = getService();
		Woche woche = sv.createNeueWoche();
		List<Tag> menu = woche.getTage();

		// Test
		waldemarBestellt(woche, menu);
		detlefBestellt(woche, menu);
	}

	@Test
	public void limit() {
		LimitOptimierung.DEBUG = true;
		try {
			// Prepare
			System.out.println("\n\n--------------------------------------------------");
			FanderService sv = getService();
			Woche woche = sv.createNeueWoche();
			List<Tag> menu = woche.getTage();
			waldemarBestellt(woche, menu);
			xxxBestellt("Detlef", woche, menu); // gleiche Bestellung nochmal - für 2. User
			freddyBestellt(woche, menu);
			
			// Test
			woche.getBestellungen().get(0).setLimit(2);
			System.out.println("Waldemar hat Limit 2 bei " + woche.getBestellungen().get(0).getBestellungen().size() + " Bestellungen.");
			FanderBestellung bestellung = sv.getFanderBestellung(woche);
			
			// Verify
			print(bestellung);
			
			// Test: getFanderBestellerStatus
			FanderBestellerStatus status = getBestellstatus4User("Waldemar", woche, bestellung, sv);
			
			// Verify
			Assert.assertEquals("getFanderBestellerStatus Test failed\r\n"
					+ status.getBestellteGerichte().stream().map(g -> "- " + g.getTitel()).collect(Collectors.joining("\r\n")) + "\r\n",
					2, status.getBestellteGerichte().size());
			System.out.println("\nBestellstatus für Waldemar: " + status.getBestellteGerichte().stream().map(g -> "\r\n- " + g.getTitel()).collect(Collectors.joining()));
		} finally {
			LimitOptimierung.DEBUG = false;
		}
	}

	// TODO Kann ich einen Fall konstruieren, bei dem
	// TODO Limittests --> Was ganz doof kommen kann: ein User benutzt Limit, aber bestellt mehrere Gerichte für einen Tag.
	
	private FanderBestellerStatus getBestellstatus4User(String user, Woche woche, FanderBestellung bestellung, FanderService sv) {
		List<FanderBestellerStatus> statusAlle = sv.getFanderBestellerStatus(woche, bestellung);
		for (FanderBestellerStatus status : statusAlle) {
			if (status.getUser().equals(user)) {
				return status;
			}
		}
		throw new RuntimeException("Bestellstatus für User " + user + " nicht gefunden!");
	}

	private void print(FanderBestellung bestellung) {
		for (FanderBestellungTag tag : bestellung.getTage()) {
			System.out.println(tag.getTag());
			for (FanderBestellungGericht g : tag.getGerichte()) {
				System.out.println("\t" + g.getAnzahl() + "x " + g.getGericht() + " / " + g.getBesteller());
			}
		}
	}

	/** Parse Testdatei (HTML). Hier wird nicht das Menü aus dem WWW geladen. Das macht den Testcase stabiler. */
	@Test
	public void parseMenu() {
		System.out.println("\n\n--------------------------------------------------");
		List<Tag> menu = new FanderMenuLoader().parseMenu(getClass().getResourceAsStream(FanderMenuLoader.DEMO_DATEI));
		Assert.assertNotNull(menu);
		Assert.assertEquals("Menü muss 5 Tage haben!", 5, menu.size());
		for (Tag tag : menu) {
			System.out.println(tag.getWochentagText());
			Assert.assertFalse("Tag hat keine Gerichte!", tag.getGerichte().isEmpty());
			for (Gericht g : tag.getGerichte()) {
				System.out.println("\t- " + g.toString());
			}
		}
		Gericht wild = menu.get(4).getGerichte().get(2);
		Assert.assertEquals("Wildragout mit Waldpilzen und Bauernspätzle", wild.getTitel());
		Assert.assertEquals("6,90", wild.getPreisFormatiert());
	}
	
	@Test
	@Ignore("Wir testen das nicht jedes Mal, da dieser Test aus dem WWW lädt.")
	public void loadWWWPage() {
		System.out.println();
		System.out.println();
		FanderConfig config = new FanderService().getConfig();
		FanderMenuLoader fanderMenuLoader = new FanderMenuLoader();
		fanderMenuLoader.loadAll();
		List<Tag> menu = fanderMenuLoader.loadMenu(config.getUrl());
		for (Tag tag : menu) {
			System.out.println("Tag #" + tag.getWochentag());
			for (Gericht g : tag.getGerichte()) {
				System.out.println("\t- " + g.toString());
			}
		}
	}
	
	@Test
	public void fanderBestellanruf() {
		// Prepare
		System.out.println("\n\n--------------------------------------------------");
		FanderService sv = getService();
		Assert.assertEquals("Mindestbestellmenge muss 2 sein!", 2, sv.getConfig().getMindestbestellmenge());
		Woche woche = sv.createNeueWoche();
		List<Tag> menu = woche.getTage();
		detlefBestellt(woche, menu);
		waldemarBestellt(woche, menu);
		
		// Test
		System.out.println();
		FanderBestellung bestellung = sv.getFanderBestellung(woche);
		for (FanderBestellungTag tag : bestellung.getTage()) {
			System.out.println(tag.getTag());
			for (FanderBestellungGericht gericht : tag.getGerichte()) {
				System.out.println("\t" + gericht.getAnzahl() + "x " + gericht.getGericht());
			}
		}
		System.out.println("Gesamtanzahl: " + bestellung.getGesamtanzahl() + " Gerichte\nGesamtpreis: " + bestellung.getGesamtpreisFormatiert() + " EUR");
		Assert.assertEquals(4, bestellung.getGesamtanzahl());
		Assert.assertEquals(26.80d, bestellung.getGesamtpreis(), 0.005d);
		
		List<FanderBestellerStatus> statusAlle = sv.getFanderBestellerStatus(woche, bestellung);
		Assert.assertEquals("Waldemar", statusAlle.get(1).getUser());
		  Assert.assertEquals(14.10d, statusAlle.get(1).getGesamtpreis(), 0.005d);
		Assert.assertEquals("Detlef", statusAlle.get(0).getUser());
		  Assert.assertEquals("12,70", statusAlle.get(0).getGesamtpreisFormatiert());
		for (FanderBestellerStatus status : statusAlle) {
			System.out.println("\n- Bestellung von " + status.getUser() + " (" + status.getGesamtpreisFormatiert() + " EUR)");
			for (Gericht gericht : status.getBestellteGerichte()) {
				System.out.println("\t- " + gericht.getWochentag().substring(0, 2) + ": " + gericht.getTitel() + " ("
						+ gericht.getPreisFormatiert() + " EUR)");
			}
		}
	}

	@Test
	public void testLimit1() {
		// Prepare
		System.out.println("\n\n--------------------------------------------------");
		FanderService sv = getService3();
		Assert.assertEquals("Mindestbestellmenge muss 3 sein!", 3, sv.getConfig().getMindestbestellmenge());
		Woche woche = sv.createNeueWoche();
		List<Tag> menu = woche.getTage();
		detlefBestellt(woche, menu);
		chantalBestellt(woche, menu);
		sonjaBestellt(woche, menu);
		
		// Test
		System.out.println();
		FanderBestellung bestellung = sv.getFanderBestellung(woche);
		for (FanderBestellungTag tag : bestellung.getTage()) {
			System.out.println(tag.getTag());
			for (FanderBestellungGericht gericht : tag.getGerichte()) {
				System.out.println("\t" + gericht.getAnzahl() + "x " + gericht.getGericht());
			}
		}
		System.out.println("Gesamtanzahl: " + bestellung.getGesamtanzahl() + " Gerichte\nGesamtpreis: " + bestellung.getGesamtpreisFormatiert() + " EUR");
		Assert.assertEquals(3, bestellung.getGesamtanzahl());
		
		List<FanderBestellerStatus> statusAlle = sv.getFanderBestellerStatus(woche, bestellung);

		for (FanderBestellerStatus status : statusAlle) {
			System.out.println("\n- Bestellung von " + status.getUser() + " (" + status.getGesamtpreisFormatiert() + " EUR)");
			for (Gericht gericht : status.getBestellteGerichte()) {
				System.out.println("\t- " + gericht.getWochentag().substring(0, 2) + ": " + gericht.getTitel() + " ("
						+ gericht.getPreisFormatiert() + " EUR)");
			}
		}

		Assert.assertEquals("Chantal", statusAlle.get(0).getUser());
		  Assert.assertEquals(6.50d, statusAlle.get(0).getGesamtpreis(), 0.005d);
		  
		Assert.assertEquals("Sonja", statusAlle.get(2).getUser());
		  Assert.assertEquals(6.50d, statusAlle.get(2).getGesamtpreis(), 0.005d);
		  
		Assert.assertEquals("Detlef", statusAlle.get(1).getUser());
		  Assert.assertEquals(7d, statusAlle.get(1).getGesamtpreis(), 0.005d);
	}

	private void detlefBestellt(Woche woche, List<Tag> menu) {
		Mitarbeiterbestellung detlef = new Mitarbeiterbestellung("Detlef");
		detlef.setLimit(2);
		detlef.add(new Gerichtbestellung(menu.get(0).getGerichte().get(1).getId())); // Montag: Wild
		// Dienstag: --
		detlef.add(new Gerichtbestellung(menu.get(2).getGerichte().get(0).getId())); // Mittwoch
		detlef.add(new Gerichtbestellung(menu.get(3).getGerichte().get(0).getId())); // Donnerstag: Cäsar-Salad
		// Freitag: --
		woche.getBestellungen().add(detlef);
	}

	private void chantalBestellt(Woche woche, List<Tag> menu) {
		Mitarbeiterbestellung chantal = new Mitarbeiterbestellung("Chantal");
		chantal.add(new Gerichtbestellung(menu.get(0).getGerichte().get(0).getId())); // Montag
		// Di --
		chantal.add(new Gerichtbestellung(menu.get(2).getGerichte().get(0).getId())); // Mittwoch
		// Do --
		// Fr --
		woche.getBestellungen().add(chantal);
	}

	private void sonjaBestellt(Woche woche, List<Tag> menu) {
		Mitarbeiterbestellung sonja = new Mitarbeiterbestellung("Sonja");
		sonja.add(new Gerichtbestellung(menu.get(0).getGerichte().get(0).getId())); // Montag: Pürree
		// Dienstag: --
		// Mittwoch: ---
		sonja.add(new Gerichtbestellung(menu.get(3).getGerichte().get(0).getId())); // Donnerstag: Cäsar-Salad
		// Freitag: --
		woche.getBestellungen().add(sonja);
	}

	private void xxxBestellt(String user, Woche woche, List<Tag> menu) {
		Mitarbeiterbestellung mb = new Mitarbeiterbestellung(user);
		mb.add(new Gerichtbestellung(menu.get(0).getGerichte().get(1).getId())); // Montag: Wild
		mb.add(new Gerichtbestellung(menu.get(1).getGerichte().get(1).getId())); // Dienstag: Schnitzel
		// Mittwoch: ---
		mb.add(new Gerichtbestellung(menu.get(3).getGerichte().get(1).getId())); // Donnerstag: Hirsch
		mb.add(new Gerichtbestellung(menu.get(4).getGerichte().get(0).getId())); // Freitag: Schnitzel
		woche.getBestellungen().add(mb);
	}

	private void waldemarBestellt(Woche woche, List<Tag> menu) {
		xxxBestellt("Waldemar", woche, menu);
	}

	private void freddyBestellt(Woche woche, List<Tag> menu) {
		Mitarbeiterbestellung mb = new Mitarbeiterbestellung("Freddy");
		mb.add(new Gerichtbestellung(menu.get(3).getGerichte().get(0).getId())); // Donnerstag: Salad
		woche.getBestellungen().add(mb);
	}

	public static class MenuLoader extends FanderMenuLoader {
		
		@Override
		public List<Tag> loadMenu(String url) {
			List<Tag> list = new ArrayList<>();

			Tag mo = new Tag(1);
			mo.add("Pürree", 6.50);
			mo.add("Wild", 7.00);
			list.add(mo);
			
			Tag di = new Tag(2);
			di.add("Gulaschsuppe", 2.00);
			di.add("Schnitzel", 5.00);
			di.add("Spätzle", 6.00);
			list.add(di);

			Tag mi = new Tag(3);
			mi.add("Knackwürste", 2.50);
			mi.add("Gemischter Salat", 2.80);
			mi.add("Schweine-Geschnetzeltes mit Kartoffelstampf", 6.80);
			list.add(mi);

			Tag don = new Tag(4);
			don.add("Cäsar-Salad", 5.70);
			don.add("Hirschbraten mit Burgundersoße", 7.10);
			list.add(don);

			Tag fr = new Tag(5);
			fr.add("Schollefilet, praktisch grätenfrei, mit Butterkartoffeln", 6.60);
			list.add(fr);
			
			return list;
		}
	}
}
