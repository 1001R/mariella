package org.mariella.rcp.resources;

import java.util.Collection;

import org.eclipse.ui.IWorkbenchWindow;

public interface VResourceManager {

void initialize();

void refererClosed(Object referer);

void refererClosed(VResourceRef ref, Object referer);

boolean removeResource(VResource resource) throws VResourceSaveException;

Collection<VResourceRef> getRefs();

Collection<VResource> getResources();

VResource createNewResource();

VResource createNewResource(VResource resource);

void addReferer(VResourceRef ref, Object referer);

VResource getResource(VResourceRef ref);

void putResource(VResourceRef ref, VResource resource);

void reload(VResourceRef ref);

void saveResource(VResource resource) throws VResourceSaveException;

void saveResource(IWorkbenchWindow window, VResource resource) throws VResourceSaveException;

VResource getResourceForPersistentId(Object persistentId);

VResourceRef getRefForPersistentId(Object persistentId);

}
