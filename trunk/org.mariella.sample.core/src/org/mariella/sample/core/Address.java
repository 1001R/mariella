package org.mariella.sample.core;

public class Address extends Entity {

private String street;
private String zipCode;
private Country country;

public String getStreet() {
	return street;
}

public void setStreet(String street) {
	Object oldValue = getStreet();
	this.street = street;
	propertyChangeSupport.firePropertyChange("street", oldValue, street);
}

public String getZipCode() {
	return zipCode;
}

public void setZipCode(String zipCode) {
	Object oldValue = getZipCode();
	this.zipCode = zipCode;
	propertyChangeSupport.firePropertyChange("zipCode", oldValue, zipCode);
}

public Country getCountry() {
	return country;
}

public void setCountry(Country country) {
	Object oldValue = getCountry();
	this.country = country;
	propertyChangeSupport.firePropertyChange("country", oldValue, country);
}

}
