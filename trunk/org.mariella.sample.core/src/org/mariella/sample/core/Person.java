package org.mariella.sample.core;

import java.util.List;

public class Person extends Entity {

private String firstName;
private String lastName;
private Character gender;
private List<Address> addresses;

public String getFirstName() {
	return firstName;
}

public void setFirstName(String firstName) {
	Object oldValue = getFirstName();
	this.firstName = firstName;
	propertyChangeSupport.firePropertyChange("firstName", oldValue, firstName);
}

public String getLastName() {
	return lastName;
}

public void setLastName(String lastName) {
	Object oldValue = getLastName();
	this.lastName = lastName;
	propertyChangeSupport.firePropertyChange("lastName", oldValue, lastName);
}

public Character getGender() {
	return gender;
}

public void setGender(Character gender) {
	Object oldValue = getGender();
	this.gender = gender;
	propertyChangeSupport.firePropertyChange("gender", oldValue, gender);
}

public List<Address> getAddresses() {
	return addresses;
}

public void setAddresses(List<Address> adressses) {
	this.addresses = adressses;
}

}
