package org.mariella.persistence.runtime;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mariella.persistence.util.Util;


public class ModificationTracker implements PropertyChangeListener {
	private Map<Object, Modifiable> participants = new HashMap<Object, Modifiable>();

	private List<ModificationInfo> modifications = new ArrayList<ModificationInfo>();
	private Map<Modifiable, ModificationInfo> modificationMap = new HashMap<Modifiable, ModificationInfo>();
	
	private List<ModificationTrackerListener> listeners = new ArrayList<ModificationTrackerListener>();
	
	private boolean enabled = true;
	
public ModificationTracker() {
	super();
}

public void addListener(ModificationTrackerListener listener) {
	listeners.add(listener);
}

public void removeListener(ModificationTrackerListener listener) {
	listeners.remove(listener);
}

public boolean isEnabled() {
	return enabled;
}

public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}

public void addNewParticipant(Modifiable participant) {
	if(participant.getIdentity() == null) {
		ModificationInfo info = new ModificationInfo(participant);
		info.setStatus(ModificationInfo.Status.New);
		addModificationInfo(info);
	} else {
		participants.put(participant.getIdentity(), participant);
	}
	participant.getPropertyChangeSupport().addPropertyChangeListener(this);
}

public void addExistingParticipant(Modifiable participant) {
	Util.assertTrue(participant.getIdentity() != null, "Must have an identity");
	if(participant.getIdentity() == null) {
		ModificationInfo info = new ModificationInfo(participant);
		info.setStatus(ModificationInfo.Status.New);
		addModificationInfo(info);
	} else {
		participants.put(participant.getIdentity(), participant);
	}
	participant.getPropertyChangeSupport().addPropertyChangeListener(this);
}

public Modifiable getParticipant(Object identity) {
	return participants.get(identity);
}

public boolean isDirty() {
	return !modifications.isEmpty();
}

public boolean isDirty(Modifiable persistable) {
	return modificationMap.containsKey(persistable); 
}

public void remove(Modifiable modifiable) {
	ModificationInfo info = modificationMap.get(modifiable);
	if(info == null) {
		info = new ModificationInfo(modifiable);
		info.setStatus(modifiable.getIdentity() == null ? ModificationInfo.Status.NewRemoved : ModificationInfo.Status.Removed);
		addModificationInfo(info);
	} else {
		if(info.getStatus() == ModificationInfo.Status.New) {
			info.setStatus(ModificationInfo.Status.NewRemoved);
		} else {
			info.setStatus(ModificationInfo.Status.Removed);
		}
	}
}

public void flushed() {
	for(ModificationInfo info : modifications) {
		if(info.getStatus() == ModificationInfo.Status.New) {
			participants.put(info.getObject().getIdentity(), info.getObject());
		} else if(info.getStatus() == ModificationInfo.Status.Removed || info.getStatus() == ModificationInfo.Status.NewRemoved) {
			participants.remove(info.getObject());
		}
	}
	modifications = new ArrayList<ModificationInfo>();
	modificationMap = new HashMap<Modifiable, ModificationInfo>();
}

public void propertyChange(PropertyChangeEvent event) {
	if(enabled) {
		Modifiable participant = (Modifiable)event.getSource();
		ModificationInfo info = modificationMap.get(participant);
		if(info == null) {
			info = new ModificationInfo(participant);
			info.setStatus(ModificationInfo.Status.Modified);
			addModificationInfo(info);
		}
		info.markModified(event.getPropertyName());
		if(event instanceof IndexedPropertyChangeEvent) {
			CollectionModificationInfo cmi = info.getCollectionModificationInfo(event.getPropertyName());
			cmi.changed((IndexedPropertyChangeEvent)event);
			for(ModificationTrackerListener listener : listeners) {
				listener.indexedPropertyChanged(participant, event.getPropertyName(), ((IndexedPropertyChangeEvent)event).getIndex(), event.getOldValue(), event.getNewValue());
			}
		} else {
			for(ModificationTrackerListener listener : listeners) {
				listener.propertyChanged(participant, event.getPropertyName(), event.getOldValue(), event.getNewValue());
			}
		}
	}
}

public void dispose() {
	for(Modifiable participant : participants.values()) {
		participant.getPropertyChangeSupport().removePropertyChangeListener(this);
	}
	participants = null;
	modifications = null;
	modificationMap = null;
}

private void addModificationInfo(ModificationInfo modificationInfo) {
	if(modificationMap.containsKey(modificationInfo.getObject())) {
		throw new IllegalArgumentException();
	}

	modifications.add(modificationInfo);
	modificationMap.put(modificationInfo.getObject(), modificationInfo);
}

public ModificationInfo getModificationInfo(Modifiable modifiable) {
	return modificationMap.get(modifiable);
}

public List<ModificationInfo> getModifications() {
	return modifications;
}

}
