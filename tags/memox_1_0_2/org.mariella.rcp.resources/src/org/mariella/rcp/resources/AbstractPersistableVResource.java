package org.mariella.rcp.resources;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public abstract class AbstractPersistableVResource extends AbstractVResource implements IPersistableElement {

public AbstractPersistableVResource() {
}

public IPersistableElement getPersistable() {
	if (ref.getPersistentId() == null) return null;	// not yet persisted
	return this;
}

public void saveState(IMemento memento) {
	 getRef().storeToMemento(memento);
}

}
