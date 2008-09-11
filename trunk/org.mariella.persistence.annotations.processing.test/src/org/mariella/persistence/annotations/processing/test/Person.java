package org.mariella.persistence.annotations.processing.test;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity(name="Person")
@Table(name="PERSON")
public class Person extends Partner {
	private static final long serialVersionUID = 1L;

	private String surname;
	private String givenName;
	private Date dateOfBirth;

@Column(name="DATEOFBIRTH", nullable=false)
public Date getDateOfBirth() {
	return dateOfBirth;
}

@Column(name="GIVENNAME", length=100, nullable=false)
public String getGivenName() {
	return givenName;
}

@Column(name="SURNAME", length=100, nullable=false)
public String getSurname() {
	return surname;
}

public void setGivenName(String givenName) {
	this.givenName = givenName;
}

public void setDateOfBirth(Date dateOfBirth) {
	this.dateOfBirth = dateOfBirth;
}

public void setSurname(String surname) {
	this.surname = surname;
}

}
