package at.hts.persistence.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ModificationInfo {

public enum Status {
	New,
	Modified,
	Removed,
	NewRemoved
}
	
	private final Modifiable object;
	private Collection<String> modifiedProperties = new HashSet<String>();
	private Map<String, CollectionModificationInfo> collectionModificationInfos = new HashMap<String, CollectionModificationInfo>();
	
	private Status status;
	
public ModificationInfo(Modifiable object) {
	super();
	this.object = object;
}

public Status getStatus() {
	return status;
}

public void setStatus(Status status) {
	this.status = status;
}

public Modifiable getObject() {
	return object;
}

public Collection<String> getModifiedProperties() {
	return modifiedProperties;
}

public CollectionModificationInfo getCollectionModificationInfo(String propertyName) {
	CollectionModificationInfo cmi = collectionModificationInfos.get(propertyName);
	if(cmi == null) {
		cmi = new CollectionModificationInfo();
		collectionModificationInfos.put(propertyName, cmi);
	}
	return cmi;
}

public void markModified(String propertyName) {
	modifiedProperties.add(propertyName);
}
	
}
