package org.mariella.oxygen.basic_impl;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.osgi.framework.Bundle;

public class BundleClassResolver implements ClassResolver {
	protected Collection<Bundle> bundles;
	
public BundleClassResolver() {
	bundles = new ArrayList<Bundle>();
	
	IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.mariella.persistence.persistenceBundles");
	for (IConfigurationElement element : elements) {
		if (element.getName().equals("bundle")) {
			String bundleId = element.getAttribute("bundleId");
			Bundle bundle = Platform.getBundle(bundleId);
			if(bundle == null) {
				throw new RuntimeException("Bundle '" + bundleId +"' cannot be resolved.");
			}
			bundles.add(bundle);
		}
	}
	
}

public Class<?> resolveClass(String className) throws ClassNotFoundException {
	for (Bundle bundle : bundles) {
		try {
			return bundle.loadClass(className);
		} catch (ClassNotFoundException ex) {
			// continue
		}
	}
	throw new ClassNotFoundException("Class '" + className + "' not found from bundles.");
}

}
