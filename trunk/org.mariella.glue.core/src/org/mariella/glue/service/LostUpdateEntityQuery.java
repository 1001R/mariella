package org.mariella.glue.service;

import org.mariella.persistence.schema.ClassDescription;
import org.springframework.jdbc.core.JdbcTemplate;



public abstract class LostUpdateEntityQuery <T extends LostUpdateEntityQueryParameter, E extends LostUpdateEntityQueryResult> extends EntityQuery<T, E> {

public LostUpdateEntityQuery(Context context, JdbcTemplate jdbcTemplate, ClassDescription classDescription, T searchParameter) {
	super(context, jdbcTemplate, classDescription, searchParameter);
}

@Override
protected void updateQueryBuilder() {
	super.updateQueryBuilder();
	
	addSelectItem("createUser", "root.createUser");
	addSelectItem("createTimestamp", "root.createTimestamp");
	addSelectItem("updateUser", "root.updateUser");
	addSelectItem("updateTimestamp", "root.updateTimestamp");
	
	if(searchParameter.getCreatedBy() != null && searchParameter.getCreatedBy().trim().length() > 0) {
		like("createdBy", "root.createUser");
	}
	
	if(searchParameter.getCreatedFrom() != null) {
		gteq("createdFrom", "root.createTimestamp");
	}

	if(searchParameter.getCreatedTo() != null) {
		lteq("createdTo", "root.createTimestamp");
	}

	if(searchParameter.getModifiedBy() != null && searchParameter.getModifiedBy().trim().length() > 0) {
		like("modifiedBy", "root.updateUser");
	}
	
	if(searchParameter.getModifiedFrom() != null) {
		gteq("modifiedFrom", "root.updateTimestamp");
	}

	if(searchParameter.getModifiedTo() != null) {
		lteq("modifiedTo", "root.updateTimestamp");
	}
}

}
