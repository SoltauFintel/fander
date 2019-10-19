package de.mwvb.fander.dao;

import de.mwvb.fander.model.FanderConfig;
import de.mwvb.maja.mongo.AbstractDAO;

public class FanderConfigDAO extends AbstractDAO<FanderConfig> {

	@Override
	protected Class<FanderConfig> getEntityClass() {
		return FanderConfig.class;
	}
}
