package org.mariella.oxygen.osgi;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.osgi.framework.Bundle;

public class LazyBundleClassResolver implements ClassResolver {

	private List<String> bundleIds;
	
	public LazyBundleClassResolver(List<String> bundleIds) {
		this.bundleIds = bundleIds;
	}

	public Class<?> resolveClass(String className) throws ClassNotFoundException {
		for (String bundleId : bundleIds) {
			Bundle bundle = Platform.getBundle(bundleId);
			if (bundle != null) {
				try {
					return bundle.loadClass(className);
				} catch (ClassNotFoundException ex) {
					// continue
				}
			} else {
				throw new RuntimeException("Bundle '" + bundleId +"' can't be resolved to load class '" + className + "'.");
			}
		}
		throw new ClassNotFoundException("Class "+className+" not found in persistence bundles: "+bundleIds);
	}

}
