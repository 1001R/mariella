package org.mariella.oxygen.spring;


import java.sql.Connection;

import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.springframework.jdbc.datasource.ConnectionHolder;

public class OxyDataSourceEntityManagerConnectionHolder extends ConnectionHolder {
	OxyServerEntityManager entityManager;

	public OxyDataSourceEntityManagerConnectionHolder(Connection conn, OxyServerEntityManager entityManager) {
		super(conn);
		this.entityManager = entityManager;
	}
	
	

}
