package org.mariella.glue.service;

import java.sql.Timestamp;


public abstract class LostUpdateEntityQueryResult extends EntityQueryResult {
	private String createUser;
	private Timestamp createTimestamp;
	private String updateUser;
	private Timestamp updateTimestamp;
	
public String getCreateUser() {
	return createUser;
}

public void setCreateUser(String createUser) {
	this.createUser = createUser;
}

public Timestamp getCreateTimestamp() {
	return createTimestamp;
}

public void setCreateTimestamp(Timestamp createTimestamp) {
	this.createTimestamp = createTimestamp;
}

public String getUpdateUser() {
	return updateUser;
}

public void setUpdateUser(String updateUser) {
	this.updateUser = updateUser;
}

public Timestamp getUpdateTimestamp() {
	return updateTimestamp;
}

public void setUpdateTimestamp(Timestamp updateTimestamp) {
	this.updateTimestamp = updateTimestamp;
}
	
}
