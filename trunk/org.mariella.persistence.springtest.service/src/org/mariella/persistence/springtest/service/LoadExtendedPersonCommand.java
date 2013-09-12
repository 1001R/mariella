package org.mariella.persistence.springtest.service;

import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.persistence.springtest.model.Person;

public class LoadExtendedPersonCommand extends Command<Void> {
	private static final long serialVersionUID = 1L;
	private Person person;
	
public LoadExtendedPersonCommand(OxyObjectPool objectPool) {
	super(objectPool);
}

public Person getPerson() {
	return person;
}

public void setPerson(Person person) {
	this.person = person;
}

}
