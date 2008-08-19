package at.hts.persistence.runtime;

import java.beans.PropertyChangeSupport;

public interface Modifiable {
public abstract Object getIdentity();
public PropertyChangeSupport getPropertyChangeSupport();
}
