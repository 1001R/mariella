package org.mariella.glue.ui;

import org.eclipse.ui.IMemento;
import org.mariella.rcp.resources.AbstractVResourceRef;

public class EntityAdapterRef extends AbstractVResourceRef {
	
public EntityAdapterRef() {
	super();
}
	
public void restoreFromMemento(IMemento memento) {
	refId = memento.getInteger("refId");
	EntityReference er = new EntityReference();
	er.setIdentity(Long.valueOf(memento.getString("persistentId")));
	er.setClassName(memento.getString("persistentClassName"));
	persistentId = er;
}

public void storeToMemento(IMemento memento) {
	memento.putInteger("refId", refId);
	if(persistentId != null) {
		EntityReference er = (EntityReference)persistentId;
		memento.putString("persistentId", ((Long)er.getIdentity()).toString());
		memento.putString("persistentClassName", er.getClassName());
	}
}

}
