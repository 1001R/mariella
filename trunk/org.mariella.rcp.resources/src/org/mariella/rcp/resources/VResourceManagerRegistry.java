package org.mariella.rcp.resources;

import java.util.ArrayList;
import java.util.List;

public class VResourceManagerRegistry {

private List<VResourceManager> managers = new ArrayList<VResourceManager>();
private List<VResourceManagerRegistryObserver> observers = new ArrayList<VResourceManagerRegistryObserver>();

VResourceManagerRegistry() {
}

public <T> T getResourceManager(Class<T> clazz) {
	for (VResourceManager mgr : managers)
		if (mgr.getClass() == clazz)
			return (T)mgr;
	throw new IllegalArgumentException();
}

public void addObserver(VResourceManagerRegistryObserver o) {
	observers.add(o);
}

public void removeObserver(VResourceManagerRegistryObserver o) {
	observers.remove(o);
}

public void addManager(VResourceManager mgr) {
	managers.add(mgr);
	try {
		mgr.initialize();
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public void preStartup() {
}

public void postStartup() {
	fireResourceManagersReady();
}

private void fireResourceManagersReady() {
	for (VResourceManagerRegistryObserver o : observers)
		o.resourceManagersReady();
}

public void close() {
}

}
