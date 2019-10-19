package de.mwvb.fander.actions;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;

import de.mwvb.fander.auth.UserService;
import de.mwvb.fander.model.Gericht;
import de.mwvb.fander.model.Tag;
import de.mwvb.fander.model.Woche;
import de.mwvb.fander.service.FanderService;
import de.mwvb.maja.web.Escaper;

public class Woche2Model {

	public void toModel(Woche woche, DataMap model, String user) {
		model.put("id", woche.getId());
		model.put("startdatum", new Escaper().esc(woche.getStartdatum()));
		model.put("startdatumNice", new Escaper().esc(woche.getStartdatumNice()));
		model.put("bestellungenErlaubt", woche.isBestellungenErlaubt() && !woche.isArchiviert());
		model.put("archiviert", woche.isArchiviert());
		model.put("anzahlGerichte", "" + woche.getAnzahlGerichte());
		double summe = 0;
		boolean sz = new UserService().getUser(user).isZusatzstoffeAnzeigen();
		
		DataList menu = model.list("menu");
		for (Tag tag : woche.getTage()) {
			DataMap map = menu.add();
			map.put("wochentag", new Escaper().esc(tag.getWochentagText()));
			map.put("anzahlGerichte", "" + tag.getAnzahlGerichte());
			map.put("hasBestellteGerichte", tag.hasBestellteGerichte());
			
			DataList gerichte = map.list("gerichte");
			for (Gericht g : tag.getGerichte()) {
				DataMap map2 = gerichte.add();
				map2.put("id", g.getId());
				String titel = g.getTitel();
				if (sz && g.getZusatzstoffe() != null && !g.getZusatzstoffe().isEmpty()) {
					titel += " (" + g.getZusatzstoffe() + ")";
				}
				map2.put("titel", new Escaper().esc(titel));
				boolean strikethru = false;
				if (sz) {
					if (g.getZusatzstoffe() == null || "?".equals(g.getZusatzstoffe()) || g.getZusatzstoffe().toUpperCase().contains("A")) {
						strikethru = true;
					}
				}
				map2.put("strikethru", strikethru);
				map2.put("zusatzstoffe", new Escaper().esc(g.getZusatzstoffe()));
				map2.put("preis", g.getPreisFormatiert());
				map2.put("preisJS", g.getPreisFormatiert().replace(",", "."));
				map2.put("bestellt", g.isBestellt());
				if (g.isBestellt()) {
					summe += g.getPreis();
				}
			}
		}
		String summeText = FanderService.format(summe);
		model.put("summe", summeText);
		model.put("summeJS", summeText.replace(",", "."));
	}
}
