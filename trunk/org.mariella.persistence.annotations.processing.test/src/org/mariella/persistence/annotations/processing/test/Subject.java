package org.mariella.persistence.annotations.processing.test;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class Subject extends Superclass {
	private static final long serialVersionUID = 1L;
	
	private String alias;
	private Boolean active = false;

@Column(name="ACTIVE")
public Boolean getActive() {
	return active;
}

@Column(name="ALIAS",length=20)
public String getAlias() {
	return alias;
}

public void setActive(Boolean active) {
	this.active = active;
}

public void setAlias(String alias) {
	this.alias = alias;
}


}
