package org.mariella.persistence.runtime;

import java.util.Collection;
import java.util.List;

import org.mariella.persistence.schema.SchemaDescription;

public interface ModificationTracker {

public SchemaDescription getSchemaDescription();
public Object getIdentity(Object participant);
public void addListener(ModificationTrackerListener listener);
public void addParticipantsListener(ModificationTrackerParticipantsListener listener);
public void removeListener(ModificationTrackerListener listener);
public void removeParticipantsListener(ModificationTrackerParticipantsListener listener);
public void addPersistentListener(ModificationTrackerListener listener);
public void removePersistentListener(ModificationTrackerListener listener);
public boolean isEnabled();
public void setEnabled(boolean enabled);
public void addNewParticipant(Object participant);
public void addExistingParticipant(Object participant);
public Object getParticipant(Object identity);
public boolean isDirty();
public boolean isDirty(Object persistable);
public void remove(Object modifiable);
public void flushed();
public void dispose();
public ModificationInfo getModificationInfo(Object modifiable);
public List<ModificationInfo> getModifications();
public boolean moveBefore(Object toBeMoved, Object target);
public boolean moveAfter(Object toBeMoved, Object target);
public Collection<?> getParticipants();
public void detachAll();
public void setHint(String key, Object value);
public Object getHint(String key);

}