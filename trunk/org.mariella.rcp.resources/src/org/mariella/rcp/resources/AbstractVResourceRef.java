package org.mariella.rcp.resources;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.ui.IMemento;

public abstract class AbstractVResourceRef implements VResourceRef {
protected Integer refId;
protected Object persistentId;
private Collection<Object> referers = new HashSet<Object>();

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

public boolean equals(Object obj) {
	if (obj == null || obj.getClass() != this.getClass()) return false;
	return ((AbstractVResourceRef)obj).refId.equals(refId);
}

public int hashCode() {
	return refId.hashCode();
}

public Collection<Object> getReferers() {
	return Collections.unmodifiableCollection(referers);
}

public void addReferer(Object referer) {
	referers.add(referer);
}

public void removeReferer(Object referer) {
	referers.remove(referer);
}

public boolean equalsPersistentId(VResourceRef ref) {
	if (persistentId == null) return false;

	return persistentId.equals(ref.getPersistentId());
}

}
