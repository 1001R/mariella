package org.mariella.persistence.springtest.service;

import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.springtest.model.Person;

public class RemotingTest extends Test {

public RemotingTest(OxyServerEntityManager entityManager) {
	super(entityManager);
}

public Person execute(LoadPersonCommand command) {
	ClusterDescription cd = new ClusterDescription(
		entityManager.getSchemaDescription().getClassDescription(Person.class.getName()), 
		"root",
		"root.addresses"
	);
	Person person = load(cd, command.getId());
	return person;
}

public void execute(LoadExtendedPersonCommand command) {
	ClusterDescription cd = new ClusterDescription(
		entityManager.getSchemaDescription().getClassDescription(Person.class.getName()), 
		"root",
		"root.friendOf",
		"root.friends",
		"root.friends.addresses",
		"root.friends.friendOf"
	);
	Person p = load(cd, command.getPerson().getId());
	new Object().toString();
}

}
