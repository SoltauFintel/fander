package de.mwvb.fander.dao;

import java.util.List;

import org.mongodb.morphia.query.FindOptions;

import de.mwvb.fander.model.Woche;
import de.mwvb.maja.mongo.AbstractDAO;

public class WocheDAO extends AbstractDAO<Woche> {

	@Override
	protected Class<Woche> getEntityClass() {
		return Woche.class;
	}

	@Override
	public List<Woche> list() {
		return createQuery()
				.order("-startdatum")
				.asList();
	}

	public Woche getJuengsteWoche() {
		FindOptions options = new FindOptions();
		options.limit(1);
		List<Woche> list = createQuery()
				.field("archiviert").equal(false)
				.order("-startdatum")
				.asList(options);
		return list.size() == 1 ? list.get(0) : null;
	}

	public Woche byStartdatum(String startdatum) {
		return createQuery().field("startdatum").equal(startdatum).get();
	}
}
