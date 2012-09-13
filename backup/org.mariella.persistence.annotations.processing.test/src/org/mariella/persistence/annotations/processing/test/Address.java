package org.mariella.persistence.annotations.processing.test;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity(name="Address")
@Table(name="ADDRESS")
public class Address extends Superclass {
	private static final long serialVersionUID = 1L;

	private String street;
	private String city;
	private List<Partner> billingAddressFor;

@OneToMany(targetEntity=Partner.class,mappedBy="billingAddress")
public List<Partner> getBillingAddressFor() {
	return billingAddressFor;
}

@Column(name="CITY",length=50,nullable=false)
public String getCity() {
	return city;
}

@Column(name="STREET",length=100,nullable=false)
public String getStreet() {
	return street;
}

public void setCity(String city) {
	this.city = city;
}

public void setBillingAddressFor(List<Partner> billingAddressFor) {
	this.billingAddressFor = billingAddressFor;
}

public void setStreet(String street) {
	this.street = street;
}

}
