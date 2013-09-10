package org.mariella.persistence.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.mariella.persistence.mapping.AbstractClassMapping;
import org.mariella.persistence.mapping.ClassMapping;
import org.mariella.persistence.persistor.ClusterDescription;
import org.mariella.persistence.runtime.MariellaPersistence;
import org.mariella.persistence.runtime.ModifiableAccessor;
import org.mariella.persistence.runtime.ModificationTracker;
import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.CollectionPropertyDescription;
import org.mariella.persistence.schema.ReferencePropertyDescription;
import org.mariella.persistence.schema.RelationshipPropertyDescription;


public class LoaderContext {
	private final ModificationTracker modificationTracker;
	private final ModifiableFactory modifiableFactory;
	private boolean isUpdate = false;
	private boolean modificationTrackerWasEnabled;
	private final JoinedCollectionManager joinedCollectionManager;
	private final Map<Object, Set<ReferencePropertyDescription>> setReferences = new HashMap<Object, Set<ReferencePropertyDescription>>();

	private final List<String> loadedRelations = new ArrayList<String>();

	private Logger logger = null;

public LoaderContext(ModificationTracker modificationTracker, ModifiableFactory modifiableFactory) {
	super();
	this.modificationTracker = modificationTracker;
	this.modifiableFactory = modifiableFactory;
	this.joinedCollectionManager = new JoinedCollectionManager(modificationTracker);
}

public LoaderContext(ModificationTracker modificationTracker) {
	this(modificationTracker, new ModifiableFactoryImpl());
}

public void startLoading() {
	modificationTrackerWasEnabled = modificationTracker.isEnabled();
	modificationTracker.setEnabled(false);
}

public void finishedLoading() {
	joinedCollectionManager.finish();
	modificationTracker.setEnabled(modificationTrackerWasEnabled);
}

public List<String> getLoadedRelations() {
	return loadedRelations;
}

public void newObject(ClusterDescription clusterDescription, String pathExpression, ClassDescription effectiveClassDescription, Object object) {
	joinedCollectionManager.newObject(clusterDescription, pathExpression, effectiveClassDescription, object);
}

public void addToRelationship(Object receiver, RelationshipPropertyDescription rpd, Object value) {
	if(rpd instanceof CollectionPropertyDescription && value != null) {
		joinedCollectionManager.addedToCollection(receiver, (CollectionPropertyDescription)rpd, value);
	}
	primitiveAddToRelationship(receiver, rpd, value);
	if(value != null && rpd.getReversePropertyDescription() instanceof ReferencePropertyDescription) {
		primitiveAddToRelationship(value, rpd.getReversePropertyDescription(), receiver);
	}
}

@SuppressWarnings("unchecked")
private void primitiveAddToRelationship(Object receiver, RelationshipPropertyDescription rpd, Object value) {
	if(rpd instanceof CollectionPropertyDescription) {
		if(value != null) {
			CollectionPropertyDescription cpd = (CollectionPropertyDescription)rpd;
			List list = (List)ModifiableAccessor.Singleton.getValue(receiver, cpd);
			if(!list.contains(value)) {
				list.add(value);
			}
		}
	} else {
		if(addToSetReferences(receiver, (ReferencePropertyDescription)rpd)) {
			ModifiableAccessor.Singleton.setValue(receiver, rpd, value);
		}
	}
}

private boolean addToSetReferences(Object receiver, ReferencePropertyDescription rpd) {
	Set<ReferencePropertyDescription> set = setReferences.get(receiver);
	if(set == null) {
		set = new HashSet<ReferencePropertyDescription>();
		setReferences.put(receiver, set);
	}
	return set.add(rpd);
}

public Object getModifiable(Object identity) {
	return modificationTracker.getParticipant(identity);
}

public Object createModifiable(ClassMapping classMapping, Object identity) {
	Object modifiable = modifiableFactory.createModifiable(classMapping.getClassDescription());
	classMapping.getClassDescription().setIdentity(modifiable, identity);
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
