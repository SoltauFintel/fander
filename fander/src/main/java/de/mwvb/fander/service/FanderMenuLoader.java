package de.mwvb.fander.service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pmw.tinylog.Logger;

import de.mwvb.fander.model.Tag;

public class FanderMenuLoader {
	public static final String DEMO_DATEI = "/xdemo/mittagskarte.html";
	private boolean loadFull_testMode = false;
	
	public List<Tag> loadMenu(String url) {
		Document doc;
		try {
			doc = Jsoup.parse(new URL(url), 60 * 1000);
		} catch (Exception e) {
			throw new RuntimeException("Fehler beim Laden des Menüs.\nURL: " + url, e);
		}
		return parseMenu(doc);
	}

	public List<Tag> parseMenu(InputStream stream) {
		Document doc;
		try {
			doc = Jsoup.parse(stream, null, "");
		} catch (Exception e) {
			throw new RuntimeException("Fehler beim Laden des Menüs.\nQuelle: InputStream", e);
		}
		return parseMenu(doc);
	}

	private List<Tag> parseMenu(Document doc) {
		//System.out.println(doc);
		List<Tag> tage = new ArrayList<>();
		for (Element day : doc.select(".dayContainer")) {
			String dayname = day.select(".title").get(0).ownText().trim();
			int wt = getWochentagNummer(dayname);
			if (!loadFull_testMode && wt < 1) {
				break;
			}
			Tag tag = new Tag(wt);
			for (Element dish : day.select(".menuName")) {
				String title = dish.ownText().trim();
				if (!title.isEmpty()) {
					Element gluten = dish.selectFirst(".text-additives");
					String glutenText = "";
					if (gluten != null && !gluten.ownText().trim().isEmpty()) {
						glutenText = gluten.ownText().trim();
					}
					tag.add(title, glutenText, 0d);
				}
			}
			int i = 0;
			for (Element e_price : day.select(".price")) {
				String priceText = e_price.text().trim();
				int o = priceText.indexOf(" ");
				if (o >= 0) {
					priceText = priceText.substring(0, o);
				}
				double price = 0;
				if (!priceText.isEmpty()) {
					price = Double.parseDouble(priceText.replace(",", "."));
				}
				tag.getGerichte().get(i++).setPreis(price);
			}
			if (!tag.getGerichte().isEmpty()) {
				tage.add(tag);
			}
		}
		return tage;
	}
	
	/**
	 * @param dayname
	 * @return 1-5; negative Zahl bedeutet dass uns der Tag nicht interessiert
	 */
	private int getWochentagNummer(String dayname) {
		switch (dayname) {
		case "Samstag": return -2;
		case "Sonntag": return -1;
		
		case "Montag": return 1;
		case "Dienstag": return 2;
		case "Mittwoch": return 3;
		case "Donnerstag": return 4;
		case "Freitag": return 5;
		
		case "Wochengerichte": return -10;
		default:
			Logger.warn("unknown dayname: " + dayname);
			return -100;
		}
	}

	public void loadAll() {
		loadFull_testMode = true;
	}
}
