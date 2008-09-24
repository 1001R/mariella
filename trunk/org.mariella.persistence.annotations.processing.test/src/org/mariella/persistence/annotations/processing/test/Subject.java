package org.mariella.persistence.annotations.processing.test;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.mariella.persistence.annotations.Converter;

@MappedSuperclass
public class Subject extends Superclass {
	private static final long serialVersionUID = 1L;
	
	private String alias;
	private Boolean active = false;
	@Transient
	private String myTransientProperty;

@Column(name="ACTIVE")
@Converter(name="BooleanConverter")
public Boolean getActive() {
	return active;
}

@Column(name="ALIAS",length=20)
@Converter(name="StringConverter")
public String getAlias() {
	return alias;
}

public void setActive(Boolean active) {
	this.active = active;
}

public void setAlias(String alias) {
	this.alias = alias;
}

public String getMyTransientProperty() {
	return myTransientProperty;
}

public void setMyTransientProperty(String myTransientProperty) {
	this.myTransientProperty = myTransientProperty;
}


}
