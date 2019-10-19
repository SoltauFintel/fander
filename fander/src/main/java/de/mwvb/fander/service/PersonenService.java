package de.mwvb.fander.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.mwvb.fander.model.KPerson;
import de.mwvb.fander.model.Person;
import de.mwvb.maja.web.AppConfig;

public class PersonenService {
	private final List<KPerson> personen = new ArrayList<>();
	
	public PersonenService() {
		// Zukünftig sollen die Personen aus der DB kommen.
		AppConfig c = new AppConfig();
		int i = 1;
		while (true) {
			String key = "" + i;
			if (key.length() < 2) {
				key = "0" + key;
			}
			key = "user" + key;
			String line = c.get(key);
			i++;
			if (line == null) {
				break;
			} else if (line.isEmpty()) {
				continue;
			}
			while (line.contains("  ")) {
				line = line.replace("  ", " ");
			}
			String w[] = line.replace("  ", " ").split(" ");
			KPerson p = new KPerson();
			p.setAusgewaehlt(line.contains(" ausgewählt"));
			p.setTypischerBesteller(line.contains(" tb"));
			p.setWeiblich("W".equalsIgnoreCase(w[0]));
			p.setVorname(w[1].replace("_", " "));
			p.setNachname(w[2].replace("_", " "));
			if (w[3].isEmpty()) {
				throw new RuntimeException("Kennwort leer!");
			}
			p.setKennwort(w[3]);
			p.setZusatzstoffeAnzeigen(line.contains(" Zusatzstoffe"));
			p.setUser(p.getVorname());
			personen.add(p);
		}
		personen.sort((a, b) -> a.getUser().toLowerCase().compareTo(b.getUser().toLowerCase()));
	}
	
	public List<KPerson> getKPersonen() {
		return personen;
	}
	
	public List<String> getTypischeBesteller() {
		return personen.stream().filter(p -> p.isTypischerBesteller()).map(p -> p.getUser()).collect(Collectors.toList());
	}
	
	public Map<String, String> getLogins() {
		Map<String, String> users = new HashMap<>();
		for (KPerson p : personen) {
			users.put(p.getUser(), p.getKennwort());
		}
		return users;
	}
	
	public List<String> getMitarbeiterliste() {
		return personen.stream().map(p -> p.getUser()).collect(Collectors.toList());
	}

	// TODO Mail Thema auf KPerson umstellen. Danach KPerson in Person umbenennen.
	public List<Person> getPersonen() {
		return this.personen.stream().map(i -> {
			Person p = new Person();
			p.setName(i.getUser());
			p.setId(p.getName().toLowerCase().replace("ü", "ue"));
			p.setAusgewaehlt(i.isAusgewaehlt());
			return p;
		}).collect(Collectors.toList());
	}
	
	public String getEmailadresse(String user) {
		try {
			return find(user).getEmailadresse() + new AppConfig().get("mail.postfix", "");
		} catch (Exception e) {
			return null;
		}
	}

	public String macheLang(String user) {
		try {
			return find(user).getName();
		} catch (Exception e) {
			// Es müssen derzeit nicht alle User unterstützt werden.
			return user;
		}
	}
	
	public boolean weiblich(String user) {
		return find(user).isWeiblich();
	}
	
	public boolean zusatzstoffeAnzeigen(String user) {
		return find(user).isZusatzstoffeAnzeigen();
	}

	private KPerson find(String user) {
		for (KPerson p : personen) {
			if (p.getUser().equalsIgnoreCase(user)) {
				return p;
			}
		}
		throw new RuntimeException("User '" + user + "' nicht vorhanden!");
	}
}
