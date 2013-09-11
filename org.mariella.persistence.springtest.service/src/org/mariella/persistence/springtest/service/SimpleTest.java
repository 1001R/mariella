package org.mariella.persistence.springtest.service;

import java.util.UUID;

import org.eclipse.core.runtime.Assert;
import org.mariella.oxygen.runtime.core.OxyServerEntityManager;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.springtest.model.Address;
import org.mariella.persistence.springtest.model.Person;

public class SimpleTest extends Test {
	
public SimpleTest(OxyServerEntityManager entityManager) {
	super(entityManager);
}

public void referentialTest() {
	Person p1 = new Person();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(p1);
	Person p2 = new Person();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(p2);
	Address a1 = new Address();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(a1);

	p1.getAddresses().add(a1);
	Assert.isTrue(a1.getPerson() == p1);
	
	a1.setPerson(p2);
	Assert.isTrue(p2.getAddresses().size() == 1);
	Assert.isTrue(p2.getAddresses().get(0) == a1);
	Assert.isTrue(p1.getAddresses().size() == 0);
}

public void step1() {
	Person person = new Person();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(person);
	person.setId("P1");
	person.setName("Hugo Boss");
	
	Address address = new Address();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(address);
	address.setId(UUID.randomUUID().toString());
	address.setDescription("Who knows?");
	address.setPerson(person);
}

public void step2() {
	ClusterDescription cd = new ClusterDescription(
		entityManager.getSchemaDescription().getClassDescription(Person.class.getName()), 
		"root",
		"root.addresses"
	);
	Person person = load(cd, "P1");
	Assert.isNotNull(person);
	Assert.isTrue(person.getAddresses().size() == 1);
	
	Person friend = new Person();
	entityManager.getObjectPool().getModificationTracker().addNewParticipant(friend);
	friend.setId("P2");
	friend.setName("Karl Lagerfeld");
	person.getFriends().add(friend);
}

public void step3() {
	ClusterDescription cd = new ClusterDescription(
		entityManager.getSchemaDescription().getClassDescription(Person.class.getName()), 
		"root",
		"root.addresses",
		"root.friends",
		"root.friends.addresses",
		"root.friends.friendOf"
	);
	Person person = load(cd, "P1");
	Assert.isNotNull(person);
	Assert.isTrue(person.getAddresses().size() == 1);
	Assert.isTrue(person.getFriends().size() == 1);
	Assert.isTrue(person.getFriends().get(0).getFriendOf().size() == 1);
}	

}
