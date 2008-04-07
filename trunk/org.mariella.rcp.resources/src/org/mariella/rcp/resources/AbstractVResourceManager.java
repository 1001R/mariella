package org.mariella.rcp.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.XMLMemento;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractVResourceManager implements VResourceManager {
private static int lastRefId = 0;

private Map<VResourceRef, VResourceRef> resourceRefInstanceMap = new HashMap<VResourceRef, VResourceRef>();
private Map<VResourceRef, VResource> resourceMap = new HashMap<VResourceRef, VResource>();
private Map<VResourceRef, List<Object>> referrersMap = new HashMap<VResourceRef, List<Object>>();


public AbstractVResourceManager() {
}

public void initialize() {}

public void removeReferrer(Object referrer) {
	for (VResourceRef ref : new ArrayList<VResourceRef>(referrersMap.keySet())) {
		removeReferrer(ref, referrer);
	}
}

public void removeReferrer(VResourceRef ref, Object referrer) {
	List<Object> referrers = referrersMap.get(ref);
	if (referrers != null) {
		referrers.remove(referrer);
		if (referrers.size() == 0)
			referrersMap.remove(ref);
		if (referrers.size() == 0) {
			VResource resource = resourceMap.remove(ref);
			resourceRefInstanceMap.remove(ref);
			if (resource != null)
				resourceRemovedFromPool(resource);
		}
	}
}

void resourceRemovedFromPool(VResource resource) {
	VResourcesPlugin.getResourcePool().fireResourceRemoved(this, resource, false);
	resource.dispose();
}

public boolean removeResource(VResource resource) throws VResourceSaveException {
	implementRemoveResource(resource);
	resourceMap.remove(resource.getRef());
	resourceRefInstanceMap.remove(resource.getRef());
	resourceRemovedFromPool(resource);
	VResourcesPlugin.getResourcePool().fireResourceRemoved(this, resource, true);
	return true;
}


public Collection<VResourceRef> getRefs() {
	return resourceMap.keySet();
}

public Collection<VResource> getResources() {
	List<VResource> loaded = new ArrayList<VResource>(resourceMap.size());
	for (VResource res : resourceMap.values())
		if (res != null)
			loaded.add(res);
	return loaded;
}

public VResource createNewResource() {
	VResource resource = implementBuildNewResource();
	VResourceRef ref = createNewRef();
	resource.setRef(ref);
	resourceMap.put(resource.getRef(), resource);
	resourceRefInstanceMap.put(resource.getRef(), resource.getRef());
	return resource;
}

public VResource createNewResource(VResource resource) {
	VResourceRef ref = createNewRef();
	resource.setRef(ref);
	resourceMap.put(resource.getRef(), resource);
	resourceRefInstanceMap.put(resource.getRef(), resource.getRef());
	return resource;
}

public void addReferrer(VResourceRef ref, Object referrer) {
	List<Object> referrers = referrersMap.get(ref);
	if (referrers == null) {
		referrers = new ArrayList<Object>();
		referrersMap.put(ref, referrers);
	}
	referrers.add(referrer);
}

public Collection<Object> getReferrers(VResourceRef ref) {
	List<Object> referrers = referrersMap.get(ref);
	if (referrers == null) return Collections.EMPTY_LIST;
	
	return Collections.unmodifiableCollection(referrers);
}

private VResourceRef createNewRef() {
	VResourceRef ref = instanciateRef();
	ref.setRefId(++lastRefId);
	return ref;
}

public VResource getResource(VResourceRef ref) {
	VResource resource = resourceMap.get(ref);
	if (resource == null) {
		if (resourceRefInstanceMap.containsKey(ref))
			ref = resourceRefInstanceMap.get(ref);
		resource = implementBuildResource(ref.getPersistentId());
		putResource(ref, resource);
	}
	return resource;
}

public void putResource(VResourceRef ref, VResource resource) {
	resource.setRef(ref);
	boolean isNew = resourceMap.get(ref) == null;
	resourceMap.put(ref, resource);
	resourceRefInstanceMap.put(ref, ref);
	if (ref.getRefId() > lastRefId)
		// if it comes from an IPersistableElement, it comes already with an id
		lastRefId = ref.getRefId();
	if (isNew)
		VResourcesPlugin.getResourcePool().fireResourceLoaded(this, resource, true);
}

public void reload(VResourceRef ref) {
	if (ref.getPersistentId() == null) return;
	
	VResource resource = resourceMap.get(ref);
	if (resource != null) {
		// only reload if in cache
		ref = resourceRefInstanceMap.get(ref);
		resource = implementBuildResource(ref.getPersistentId());
		resource.setRef(ref);
		resourceMap.put(ref, resource);
		VResourcesPlugin.getResourcePool().fireResourceLoaded(this, resource, true);
	}
}


public void saveResource(VResource resource) throws VResourceSaveException {
	Object persistentId = implementSaveResource(resource);
	if (persistentId != null) {
		resource.getRef().setPersistentId(persistentId);
		VResourcesPlugin.getResourcePool().fireResourceChanged(this, resource, true);
	}
}

public void saveResource(IWorkbenchWindow window, VResource resource) throws VResourceSaveException {
	saveResource(resource);
}

public VResource getResourceForPersistentId(Object persistentId) {
	VResourceRef ref = getRefForPersistentId(persistentId);
	return getResource(ref);
}

public VResourceRef getRefForPersistentId(Object persistentId) {
	for (VResourceRef ref : resourceMap.keySet())
		if (persistentId.equals(ref.getPersistentId()))
			return ref;
	VResourceRef ref = createNewRef();
	ref.setPersistentId(persistentId);
	resourceMap.put(ref, null);
	resourceRefInstanceMap.put(ref, ref);
	return ref;
}

protected abstract VResource implementBuildResource(Object persistentId);

protected abstract VResource implementBuildNewResource();

/**
 * Implementors must return the Persistent ID of the resource
 * 
 * @param resource
 * @return
 * @throws VResourceSaveException
 */
protected abstract Object implementSaveResource(VResource resource) throws VResourceSaveException;

protected void implementRemoveResource(VResource resource) throws VResourceSaveException {
	throw new UnsupportedOperationException();
}

protected abstract VResourceRef instanciateRef();

protected IMemento createMemento() {
	Document doc;
	try {
		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	} catch (ParserConfigurationException e) {
		throw new RuntimeException(e);
	}
	Element el = doc.createElement("root");
	doc.appendChild(el);
	return new XMLMemento(doc, el);
}

}
