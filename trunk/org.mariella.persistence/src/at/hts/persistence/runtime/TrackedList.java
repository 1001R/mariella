package at.hts.persistence.runtime;

import java.beans.PropertyChangeSupport;

public class TrackedList<T extends Modifiable> extends ObservableList<T> {
	private final Modifiable owner;
	
public TrackedList(Modifiable owner, String propertyName) {
	super(propertyName);
	this.owner = owner;
}

@Override
protected PropertyChangeSupport getPropertyChangeSupport() {
	return owner.getPropertyChangeSupport();
}
	

}
