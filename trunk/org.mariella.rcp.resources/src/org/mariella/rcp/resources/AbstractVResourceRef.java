package org.mariella.rcp.resources;

import org.eclipse.ui.IMemento;

public abstract class AbstractVResourceRef implements VResourceRef {
protected Integer refId;
protected Object persistentId;

public AbstractVResourceRef() {
}

public Integer getRefId() {
	return refId;
}

public void setRefId(Integer refId) {
	this.refId = refId;
}

public Object getPersistentId() {
	return persistentId;
}

public void setPersistentId(Object persistentId) {
	this.persistentId = persistentId;
}

public abstract void restoreFromMemento(IMemento memento);

public abstract void storeToMemento(IMemento memento);

@Override
public boolean equals(Object obj) {
	if (obj == null || obj.getClass() != this.getClass()) return false;
	return ((AbstractVResourceRef)obj).refId.equals(refId);
}

@Override
public int hashCode() {
	return refId.hashCode();
}

public boolean equalsPersistentId(VResourceRef ref) {
	if (persistentId == null) return false;

	return persistentId.equals(ref.getPersistentId());
}

}
