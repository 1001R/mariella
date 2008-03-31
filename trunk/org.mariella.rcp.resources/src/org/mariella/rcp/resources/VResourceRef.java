package org.mariella.rcp.resources;

import java.util.Collection;

import org.eclipse.ui.IMemento;

public interface VResourceRef {

Integer getRefId();

Object getPersistentId();

void storeToMemento(IMemento memento);

void restoreFromMemento(IMemento memento);

void setRefId(Integer refId);

void setPersistentId(Object persistentId);

Collection<Object> getReferers();

void addReferer(Object referer);

void removeReferer(Object referer);

boolean equalsPersistentId(VResourceRef ref);

}
