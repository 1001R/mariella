package at.hts.persistence.test.model;


public abstract class Adresse extends Superclass {
	private String strasse;
	private Person person;

public String getStrasse() {
	return strasse;
}

public void setStrasse(String strasse) {
	String oldValue = this.strasse;
	this.strasse = strasse;
	propertyChangeSupport.firePropertyChange("strasse", oldValue, strasse);
}

public Person getPerson() {
	return person;
}

public void setPerson(Person person) {
	Person oldValue = this.person;
	this.person = person;
	propertyChangeSupport.firePropertyChange("person", oldValue, person);
}
	
}
