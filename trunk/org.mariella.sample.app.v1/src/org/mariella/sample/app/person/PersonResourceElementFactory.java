package org.mariella.sample.app.person;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.mariella.rcp.resources.VResourcesPlugin;

public class PersonResourceElementFactory implements IElementFactory {
public static final String ID = PersonResourceElementFactory.class.getName();

public IAdaptable createElement(IMemento memento) {
	PersonResourceRef ref = new PersonResourceRef();
	ref.restoreFromMemento(memento);
	PersonResourceManager mgr = VResourcesPlugin.getResourceManagerRegistry().getResourceManager(PersonResourceManager.class);
	PersonResource resource = (PersonResource)mgr.getResource(ref);
	return resource;
}

}
