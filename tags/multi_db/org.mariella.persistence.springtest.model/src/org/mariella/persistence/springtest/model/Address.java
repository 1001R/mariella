package org.mariella.persistence.springtest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="ADDRESS")
public class Address extends Superclass implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String description;
	private Person person;
	
@Column(name="DESCRIPTION")
public String getDescription() {
	return description;
}

public void setDescription(String description) {
	String old = this.description;
	this.description = description;
	propertyChangeSupport.firePropertyChange("description", old, description);
}

@ManyToOne
@JoinColumn(name="PERSON_ID", referencedColumnName="ID")
public Person getPerson() {
	return person;
}

public void setPerson(Person person) {
	Person old = this.person;
	this.person = person;
	propertyChangeSupport.firePropertyChange("person", old, person);
}

}
