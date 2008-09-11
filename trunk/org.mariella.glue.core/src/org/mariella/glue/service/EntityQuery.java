package org.mariella.glue.service;

import org.springframework.jdbc.core.JdbcTemplate;

import at.hts.persistence.schema.ClassDescription;


public abstract class EntityQuery<T, E extends EntityQueryResult> extends Query<T, E>{
	
public EntityQuery(Context context, JdbcTemplate jdbcTemplate, ClassDescription classDescription, T searchParameter) {
	super(context, jdbcTemplate, classDescription, searchParameter);
}

protected void updateQueryBuilder() {
	addSelectItem("id", "root.id");
}

}
