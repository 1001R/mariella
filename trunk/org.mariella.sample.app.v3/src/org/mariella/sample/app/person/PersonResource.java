package org.mariella.sample.app.person;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemResourceHolder;
import org.mariella.rcp.resources.AbstractVResource;
import org.mariella.sample.core.Address;
import org.mariella.sample.core.Person;

public class PersonResource extends AbstractVResource implements IAdaptable, IEditorInput, PersonResourceRefHolder, 
	PropertyChangeListener, ProblemResourceHolder {

private Person person;

public PersonResource(Person person) {
	this.person = person;
	person.addPropertyChangeListener(this);
	for (Address a : person.getAddresses())
		a.addPropertyChangeListener(this);
}

public Address addAddress() {
	Address address = new Address();
	address.addPropertyChangeListener(this);
	person.getAddresses().add(address);
	return address;
}

public void removeAddress(Address a) {
	a.removePropertyChangeListener(this);
	person.getAddresses().remove(a);
}

/**
 * See IAdaptable interface for further description.
 */
@Override
public Object getAdapter(Class adapter) {
	// we don't work with adapters
	return null;
}

/**
 * See IEditorInput interface for further description.
 * 
 */
@Override
public boolean exists() {
	return false;
}

@Override
public ImageDescriptor getImageDescriptor() {
	return null;
}

@Override
public String getName() {
	return "Person";
}

/**
 * See IEditorInput interface for further description.
 */
@Override
public IPersistableElement getPersistable() {
	return null;
}

/**
 * See IEditorInput interface for further description.
 */
@Override
public String getToolTipText() {
	return getName();
}

/**
 * PropertyChangeListener implementation that notifies listeners
 * that the resource has been changed by the user.
 */
@Override
public void propertyChange(PropertyChangeEvent evt) {
	fireChanged();
}

public Person getPerson() {
	return person;
}

@Override
public ProblemResource getProblemResource() {
	return new PersonProblemResource((PersonResourceRef)getRef(), getName());
}

}
