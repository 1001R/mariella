package org.mariella.rcp.resources;

public interface VResourceChangeListener {

void resourceChanged(VResourceChangeEvent event);

void resourceRemoved(VResourceChangeEvent event);

void resourceLoaded(VResourceChangeEvent event);

}
