package org.mariella.persistence.runtime;

public interface ModificationTrackerEntityListener {

public void participantAdded(Object participant, ModificationInfo info);
public void participantRemoved(Object participant, ModificationInfo info);
public void participantFlushed(Object participant, ModificationInfo info);
	
}
