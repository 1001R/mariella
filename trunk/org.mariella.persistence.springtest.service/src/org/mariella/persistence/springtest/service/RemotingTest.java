package org.mariella.persistence.springtest.service;

import java.util.UUID;

import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.springtest.model.Address;
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
	load(cd, command.getPerson().getId());
}

public void execute(CreateTestDataCommand command) {
	Person person = new Person();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(person);
	person.setId("P1");
	person.setName("Hugo Boss");
	
	Address address = new Address();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(address);
	address.setId(UUID.randomUUID().toString());
	address.setDescription("Who knows?");
	address.setPerson(person);

	Person friend = new Person();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(friend);
	friend.setId("P2");
	friend.setName("Karl Lagerfeld");
	person.getFriends().add(friend);
}

}
