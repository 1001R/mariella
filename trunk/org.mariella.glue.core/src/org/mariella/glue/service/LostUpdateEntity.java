package org.mariella.glue.service;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class LostUpdateEntity extends Entity {
	private String createUser;
	private Timestamp createTimestamp;
	private String updateUser;
	private Timestamp updateTimestamp;
	
@Column(name="CREATE_USER")
public String getCreateUser() {
	return createUser;
}

public void setCreateUser(String createUser) {
	String old = this.createUser;
	this.createUser = createUser;
	propertyChangeSupport.firePropertyChange("createUser", old, createUser);
}

@Column(name="CREATE_TIMESTAMP")
public Timestamp getCreateTimestamp() {
	return createTimestamp;
}

public void setCreateTimestamp(Timestamp createTimestamp) {
	Timestamp old = this.createTimestamp;
	this.createTimestamp = createTimestamp;
	propertyChangeSupport.firePropertyChange("createTimestamp", old, createTimestamp);
}

@Column(name="UPDATE_USER")
public String getUpdateUser() {
	return updateUser;
}

public void setUpdateUser(String updateUser) {
	String old = this.updateUser;
	this.updateUser = updateUser;
	propertyChangeSupport.firePropertyChange("updateUser", old, updateUser);
}

@Column(name="UPDATE_TIMESTAMP")
public Timestamp getUpdateTimestamp() {
	return updateTimestamp;
}

public void setUpdateTimestamp(Timestamp updateTimestamp) {
	Timestamp old = this.updateTimestamp;
	this.updateTimestamp = updateTimestamp;
	propertyChangeSupport.firePropertyChange("updateTimestamp", old, updateTimestamp);
}

}
