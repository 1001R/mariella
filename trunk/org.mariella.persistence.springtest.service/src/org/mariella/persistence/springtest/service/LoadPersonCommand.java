package org.mariella.persistence.springtest.service;

import org.mariella.oxygen.basic_core.OxyObjectPool;
import org.mariella.persistence.springtest.model.Person;

public class LoadPersonCommand extends Command<Person> {
	private static final long serialVersionUID = 1L;
	
	private String id;
	
public LoadPersonCommand(OxyObjectPool objectPool) {
	super(objectPool);
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

}
