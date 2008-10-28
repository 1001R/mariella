package org.mariella.persistence.loader;

import java.util.List;
import java.util.logging.Logger;

import org.mariella.persistence.mapping.AbstractClassMapping;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.runtime.MariellaPersistence;
import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.runtime.ModificationTracker;
import org.mariella.persistence.schema.CollectionPropertyDescription;
import org.mariella.persistence.schema.ReferencePropertyDescription;
import org.mariella.persistence.schema.RelationshipPropertyDescription;


public class LoaderContext {
	private final ModificationTracker modificationTracker;
	private final ModifiableFactory modifiableFactory;
	private boolean isUpdate = false;
	private boolean modificationTrackerWasEnabled;
	
	private Logger logger = null;
	
public LoaderContext(ModificationTracker modificationTracker, ModifiableFactory modifiableFactory) {
	super();
	this.modificationTracker = modificationTracker;
	this.modifiableFactory = modifiableFactory;
}

public LoaderContext(ModificationTracker modificationTracker) {
	this(modificationTracker, new ModifiableFactoryImpl());
}

public void startLoading() {
	modificationTrackerWasEnabled = modificationTracker.isEnabled();
	modificationTracker.setEnabled(false);
}

public void finishedLoading() {
	modificationTracker.setEnabled(modificationTrackerWasEnabled);
}

public void addToRelationship(Object receiver, RelationshipPropertyDescription rpd, Object value) {
	primitiveAddToRelationship(receiver, rpd, value);
	if(rpd.getReversePropertyDescription() instanceof ReferencePropertyDescription) {
		primitiveAddToRelationship(value, rpd.getReversePropertyDescription(), receiver);
	}
}

@SuppressWarnings("unchecked")
private void primitiveAddToRelationship(Object receiver, RelationshipPropertyDescription rpd, Object value) {
	if(rpd instanceof CollectionPropertyDescription) {
		List list = (List)ModifiableAccessor.Singleton.getValue(receiver, rpd);
		if(!list.contains(value)) {
			list.add(value);
		}
	} else {
		ModifiableAccessor.Singleton.setValue(receiver, rpd, value);
	}
}
	
public Modifiable getModifiable(Object identity) {
	return modificationTracker.getParticipant(identity);
}

public Modifiable createModifiable(ClassMapping classMapping, Object identity) {
	Modifiable modifiable = modifiableFactory.createModifiable(classMapping.getClassDescription());
	ModifiableAccessor.Singleton.setValue(modifiable, classMapping.getIdMapping().getPropertyDescription(), identity);
	modificationTracker.addExistingParticipant(modifiable);
	return modifiable;
}

public Object createEmbeddable(AbstractClassMapping classMapping) {
	return modifiableFactory.createEmbeddable(classMapping.getClassDescription());
}

public boolean isUpdate() {
	return isUpdate;
}

public void setUpdate(boolean isUpdate) {
	this.isUpdate = isUpdate;
}

public Logger getLogger() {
	if(logger == null) {
		logger = MariellaPersistence.logger;
	}
	return logger;
}

public void setLogger(Logger logger) {
	this.logger = logger;
}

}
