package org.mariella.sample.app.person;

import java.util.ArrayList;

import org.eclipse.ui.IEditorInput;
import org.mariella.rcp.resources.AbstractEditorAwareVResourceManager;
import org.mariella.rcp.resources.VResource;
import org.mariella.rcp.resources.VResourceRef;
import org.mariella.rcp.resources.VResourceSaveException;
import org.mariella.sample.core.Address;
import org.mariella.sample.core.Person;
import org.mariella.sample.core.SampleCorePlugin;

public class PersonResourceManager extends AbstractEditorAwareVResourceManager {

public PersonResourceManager() {
	super(PersonEditor.ID, PersonResourceElementFactory.ID);
}

@Override
public IEditorInput getEditorInput(VResource resource) {
	return (PersonResource)resource;
}

@Override
protected VResource implementBuildNewResource() {
	Person person = new Person();
	person.setAddresses(new ArrayList<Address>());
	return new PersonResource(person);
}

@Override
protected VResource implementBuildResource(Object persistentId) {
	Person person = SampleCorePlugin.getCoreService().getPerson((Integer)persistentId);
	return new PersonResource(person);
}

@Override
protected Object implementSaveResource(VResource resource) throws VResourceSaveException {
	Person person = ((PersonResource)resource).getPerson();
	SampleCorePlugin.getCoreService().savePerson(person);
	return person.getId();
}

@Override
protected VResourceRef instanciateRef() {
	return new PersonResourceRef();
}

}
