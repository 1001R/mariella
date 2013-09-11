package org.mariella.persistence.runtime;

public interface ModificationTrackerListener {

public void propertyChanged(Object modifiable, String propertyName, Object oldValue, Object newValue);
public void indexedPropertyChanged(Object modifiable, String propertyName, int index, Object oldValue, Object newValue);
}
