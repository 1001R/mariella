package org.mariella.rcp.resources;

public interface VResourceChangeListener {

void resourceChanged(VResourceChangeEvent event);

void resourceRemovedFromPool(VResourceChangeEvent event);

void resourceLoaded(VResourceChangeEvent event);

}
