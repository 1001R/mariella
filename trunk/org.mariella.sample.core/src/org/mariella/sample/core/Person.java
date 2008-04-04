package org.mariella.sample.core;

import java.util.List;

public class Person extends Entity {

private String firstName;
private String lastName;
private Character gender; 	// (m)ale / (f)email)
private Boolean buddy;		
private Character maritalStatus;	// (m)arried / (s)ingle / (w)idow / (d)ivorcee
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

public Boolean getBuddy() {
	return buddy;
}

public void setBuddy(Boolean buddy) {
	Object oldValue = getBuddy();
	this.buddy = buddy;
	propertyChangeSupport.firePropertyChange("buddy", oldValue, buddy);
}

public Character getMaritalStatus() {
	return maritalStatus;
}

public void setMaritalStatus(Character maritalStatus) {
	Object oldValue = getMaritalStatus();
	this.maritalStatus = maritalStatus;
	propertyChangeSupport.firePropertyChange("buddy", oldValue, buddy);
}

}
