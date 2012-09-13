package org.mariella.rcp.resources;

import org.eclipse.ui.IMemento;

public interface VResourceRef {

Integer getRefId();

Object getPersistentId();

void storeToMemento(IMemento memento);

void restoreFromMemento(IMemento memento);

void setRefId(Integer refId);

void setPersistentId(Object persistentId);

boolean equalsPersistentId(VResourceRef ref);

}
