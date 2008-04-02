package org.mariella.sample.app.person;

import org.eclipse.ui.IMemento;
import org.mariella.rcp.resources.AbstractVResourceRef;

public class PersonResourceRef extends AbstractVResourceRef {

@Override
public void restoreFromMemento(IMemento memento) {
	refId = memento.getInteger("refId");
	persistentId = memento.getInteger("persistentId");
}

@Override
public void storeToMemento(IMemento memento) {
	memento.putInteger("refId", refId);
	if (persistentId != null)
		memento.putInteger("persistentId", (Integer)persistentId);
}

}
