package at.hts.persistence.runtime;

public interface ModificationTrackerListener {

public void propertyChanged(Modifiable modifiable, String propertyName, Object oldValue, Object newValue);
public void indexedPropertyChanged(Modifiable modifiable, String propertyName, int index, Object oldValue, Object newValue);
}
