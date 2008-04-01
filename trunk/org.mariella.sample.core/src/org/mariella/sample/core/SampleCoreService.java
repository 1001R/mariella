package org.mariella.sample.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SampleCoreService {

List<Country> countries = new ArrayList<Country>();
List<Person> persons = new ArrayList<Person>();

{
	Country usa = new Country();
	usa.setIsoCode("USA");
	usa.setName("United States");
	countries.add(usa);
	
	Country atu = new Country();
	atu.setIsoCode("ATU");
	atu.setName("Austria");
	countries.add(atu);
	
	Person paul = new Person();
	paul.setFirstName("Paul");
	paul.setLastName("Newman");
	paul.setGender('m');
	paul.setAddresses(new ArrayList<Address>());
	persons.add(paul);
	
	Address paulAddress = new Address();
	paulAddress.setStreet("5th Avenue, Suite C; 10128 New York");
	paulAddress.setCountry(usa);
	paul.getAddresses().add(paulAddress);
}


public List<Person> getAvailablePersons() {
	List<Person> result = new ArrayList<Person>(persons.size());
	for (Person p : persons) {
		result.add(copyPerson(p));
	}
	return result;
}

public List<Country> getAvailableCountries() {
	return countries;
}

public void savePerson(Person p) {
	// find "ori" person having same id
	Person ori = null;
	for (Person each : persons) {
		if (each.getId().equals(p.getId())) {
			ori = each;
			break;
		}
	}

	if (ori == null) {
		// person not yet exists, add copy to persons
		persons.add(copyPerson(p));
		return;
	} else {
		// person exists, update original instance
		ori.setId(p.getId());
		ori.setFirstName(p.getFirstName());
		ori.setLastName(p.getLastName());
		ori.setGender(p.getGender());
		
		List<Address> remainingAddresses = new ArrayList<Address>(ori.getAddresses());
		for (Address a : p.getAddresses()) {
			Address oriA = getAddress(ori, a.getId());
			if (oriA != null) {
				// address update 
				remainingAddresses.remove(oriA);
				oriA.setCountry(a.getCountry());
				oriA.setStreet(a.getStreet());
				oriA.setZipCode(a.getZipCode());
			} else {
				// address insert
				ori.getAddresses().add(copyAddress(a));
			}
		}
		// for all addresses that do not appear any more...
		for (Address remainingA : remainingAddresses) {
			// address delete
			ori.getAddresses().remove(remainingA);
		}
	}
}

private Address getAddress(Person p, Integer id) {
	for (Address a : p.getAddresses())
		if (a.getId().equals(id))
			return a;
	return null;
}

public Person getPerson(Integer id) {
	for (Person p : persons) {
		if (p.getId().equals(id))
			return copyPerson(p);
	}
	return null;
}

private Person copyPerson(Person p) {
	Person pCopy = new Person();
	pCopy.setId(p.getId());
	pCopy.setFirstName(p.getFirstName());
	pCopy.setLastName(p.getLastName());
	pCopy.setGender(p.getGender());
	pCopy.setAddresses(new ArrayList<Address>(p.getAddresses().size()));
	for (Address a : p.getAddresses()) {
		pCopy.getAddresses().add(copyAddress(a));
	}
	return pCopy;
}

private Address copyAddress(Address a) {
	Address aCopy = new Address();
	aCopy.setId(a.getId());
	aCopy.setStreet(a.getStreet());
	aCopy.setZipCode(a.getZipCode());
	aCopy.setCountry(a.getCountry());	// we don't make a copy of country, because this is not edited in our sample application
	return aCopy;
}

public void removePerson(Person person) {
	for (Iterator<Person> it = persons.iterator(); it.hasNext();)
		if (it.next().getId().equals(person.getId()))
			it.remove();
}

}
