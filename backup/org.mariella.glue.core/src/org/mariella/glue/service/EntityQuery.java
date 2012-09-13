package org.mariella.glue.service;

import org.mariella.persistence.schema.ClassDescription;
import org.springframework.jdbc.core.JdbcTemplate;



public abstract class EntityQuery<T, E extends EntityQueryResult> extends Query<T, E>{
	
public EntityQuery(Context context, JdbcTemplate jdbcTemplate, ClassDescription classDescription, T searchParameter) {
	super(context, jdbcTemplate, classDescription, searchParameter);
}

protected void updateQueryBuilder() {
	addSelectItem("id", "root.id");
}

}
