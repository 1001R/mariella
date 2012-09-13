package org.mariella.glue.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class EntityAdapterElementFactory implements IElementFactory {
	public static final String ID = EntityAdapterElementFactory.class.getName();
	
public IAdaptable createElement(IMemento memento) {
	EntityAdapterRef ref = new EntityAdapterRef();
	ref.restoreFromMemento(memento);
	
	EntityReference ei = (EntityReference)ref.getPersistentId();
	EntityAdapterResourceManager<?> mgr = UIRegistry.Singleton.get(ei.getClassName()).getResourceManager();
	return (IAdaptable)mgr.getResource(ref);
}

}
