package org.mariella.rcp.resources;

import java.util.Collection;

import org.eclipse.ui.IWorkbenchWindow;

public interface VResourceManager {

void addReferrer(VResourceRef ref, Object referrer);

VResource createNewResource();

VResource createNewResource(VResource resource);

Collection<Object> getReferrers(VResourceRef ref);

VResourceRef getRefForPersistentId(Object persistentId);

Collection<VResourceRef> getRefs();

VResource getResource(VResourceRef ref);

VResource getResourceForPersistentId(Object persistentId);

Collection<VResource> getResources();

void initialize();

void putResource(VResourceRef ref, VResource resource);

void removeReferrer(Object referrer);

void removeReferrer(VResourceRef ref, Object referrer);

void reload(VResourceRef ref);

boolean removeResource(VResource resource) throws VResourceSaveException;

void saveResource(IWorkbenchWindow window, VResource resource) throws VResourceSaveException;

void saveResource(VResource resource) throws VResourceSaveException;

}
