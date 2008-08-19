package at.hts.persistence.test.model;

import java.util.List;

import at.hts.persistence.runtime.TrackedList;

public class Person extends Superclass {
	private Identity identity = new Identity("unnamed", 0);
	private Person contactPerson;
	private Person contactPersonFor;
	private List<Adresse> adressen = new TrackedList<Adresse>(this, "adressen");
	private List<Adresse> privatAdressen = new TrackedList<Adresse>(this, "privatAdressen");


public Identity getPersonIdentity() {
	return identity;
}

public void setPersonIdentity(Identity identity) {
	Identity old = this.identity;
	this.identity = identity;
	propertyChangeSupport.firePropertyChange("personIdentity", old, identity);
}

public List<Adresse> getAdressen() {
	return adressen;
}

public List<Adresse> getPrivatAdressen() {
	return privatAdressen;
}

public Person getContactPerson() {
	return contactPerson;
}

public void setContactPerson(Person contactPerson) {
	Person old = this.contactPerson;
	this.contactPerson = contactPerson;
	propertyChangeSupport.firePropertyChange("contactPerson", old, contactPerson);
}

public Person getContactPersonFor() {
	return contactPersonFor;
}

public void setContactPersonFor(Person contactPersonFor) {
	Person old = this.contactPersonFor;
	this.contactPersonFor = contactPersonFor;
	propertyChangeSupport.firePropertyChange("contactPersonFor", old, contactPersonFor);
}

}
