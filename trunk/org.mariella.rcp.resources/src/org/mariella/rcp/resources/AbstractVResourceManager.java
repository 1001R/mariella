package org.mariella.rcp.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.XMLMemento;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractVResourceManager {
private static int lastRefId = 0;

private Map<VResourceRef, VResourceRef> resourceRefInstanceMap = new HashMap<VResourceRef, VResourceRef>();
private Map<VResourceRef, VResource> resourceMap = new HashMap<VResourceRef, VResource>();


public AbstractVResourceManager() {
}

public void initialize() {}

public void refererClosed(Object referer) {
	Collection<VResourceRef> refs = getRefsHavingReferer(referer);
	for (VResourceRef ref : refs) {
		refererClosed(ref, referer);
	}
}

public void refererClosed(VResourceRef ref, Object referer) {
	ref.removeReferer(referer);
	if (ref.getReferers().size() == 0) {
		VResource resource = resourceMap.remove(ref);
		resourceRefInstanceMap.remove(ref);
		if (resource != null)
			resourceRemovedFromPool(resource);
	}
}

void resourceRemovedFromPool(VResource resource) {
	VResourcesPlugin.getResourcePool().fireResourceRemovedFromPool(this, resource);
	resource.dispose();
}

public boolean removeResource(VResource resource) throws VResourceSaveException {
	implementRemoveResource(resource);
	resourceMap.remove(resource.getRef());
	resourceRefInstanceMap.remove(resource.getRef());
	resourceRemovedFromPool(resource);
	return true;
}

public void removeResourceFromPool(VResource resource) {
	resourceMap.remove(resource.getRef());
	resourceRefInstanceMap.remove(resource.getRef());
	resourceRemovedFromPool(resource);
}

private void resourceLoaded(VResource resource) {
	VResourcesPlugin.getResourcePool().fireResourceLoaded(this, resource);
}

public void resourceChanged(VResource resource) {
	VResourcesPlugin.getResourcePool().fireResourceChanged(this, resource);
}

private Collection<VResourceRef> getRefsHavingReferer(Object referer) {
	Set<VResourceRef> refs = new HashSet<VResourceRef>();
	for (VResourceRef ref : resourceMap.keySet()) {
		if (ref.getReferers().contains(referer))
			refs.add(ref);
	}
	return refs;
}

protected void putRef(Object persistentId) {
	getRefForPersistentId(persistentId);
}

public Collection<VResourceRef> getAppearingRefs() {
	return resourceMap.keySet();
}

public Collection<VResource> getLoadedResources() {
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

public void addReferer(VResourceRef ref, Object referer) {
	ref.addReferer(referer);
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
		resource.setRef(ref);
		resourceMap.put(ref, resource);
		resourceRefInstanceMap.put(ref, ref);
		if (ref.getRefId() > lastRefId)
			// if it comes from an IPersistableElement, it comes already with an id
			lastRefId = ref.getRefId();
		resourceLoaded(resource);
	}
	return resource;
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
		resourceLoaded(resource);
	}
}


public void saveResource(VResource resource) throws VResourceSaveException {
	Object persistentId = implementSaveResource(resource);
	if (persistentId != null) {
		resource.getRef().setPersistentId(persistentId);
		VResourcesPlugin.getResourcePool().fireResourceChanged(this, resource);
	}
}

public void saveResource(IWorkbenchWindow window, VResource resource) throws VResourceSaveException {
	saveResource(resource);
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

protected void preStartup() {}

public void postStartup() {}

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
