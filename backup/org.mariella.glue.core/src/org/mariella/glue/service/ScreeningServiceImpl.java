package org.mariella.glue.service;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class ScreeningServiceImpl implements ScreeningService {
	protected JdbcTemplate jdbcTemplate;
	
public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
	this.jdbcTemplate = jdbcTemplate;
}

}
