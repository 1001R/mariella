package org.mariella.sample.app.person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.ui.IEditorInput;
import org.mariella.rcp.problems.ProblemManager;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemsProvider;
import org.mariella.rcp.resources.AbstractEditorAwareVResourceManager;
import org.mariella.rcp.resources.VResource;
import org.mariella.rcp.resources.VResourceRef;
import org.mariella.rcp.resources.VResourceSaveException;
import org.mariella.sample.core.Address;
import org.mariella.sample.core.Person;
import org.mariella.sample.core.SampleCorePlugin;

import at.mariella.test.MyPersonProblemResource;

public class PersonResourceManager extends AbstractEditorAwareVResourceManager implements ProblemsProvider {

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
protected void implementRemoveResource(VResource resource) throws VResourceSaveException {
	Person person = ((PersonResource)resource).getPerson();
	SampleCorePlugin.getCoreService().removePerson(person);
}

@Override
protected VResourceRef instanciateRef() {
	return new PersonResourceRef();
}

@Override
public void addProblems(ProblemManager problemMgr, ProblemResource resource) {
	// TODO Auto-generated method stub
	
}

@Override
public List<ProblemResource> getProblemResources() {
	Collection<VResource> loaded = getLoadedResources();
	List<ProblemResource> resources = new ArrayList<ProblemResource>();
	for(VResource resource : loaded) {
		resources.add(new PersonProblemResource(resource.getRef(), ((PersonResource)resource).getName());
	}
	return resources;
}

}
