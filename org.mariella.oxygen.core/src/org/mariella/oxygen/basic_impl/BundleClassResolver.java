package org.mariella.oxygen.basic_impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRevision;

public class BundleClassResolver implements ClassResolver {

	private Collection<Bundle> bundles;
	
	public BundleClassResolver(Collection<String> bundleIds) {
		Bundle[] bundles = new Bundle[bundleIds.size()];
		int i = 0;
		for(String bundleId : bundleIds) {
			Bundle bundle = Platform.getBundle(bundleId);
			if(bundle == null) {
				throw new RuntimeException("Bundle '" + bundleId +"' cannot be resolved.");
			}
			bundles[i++] = bundle;
		}
		this.bundles = getTopLevelBundles(bundles);
	}
	
	public BundleClassResolver(Bundle... bundles) {
		this.bundles = getTopLevelBundles(bundles);
	}
	
	@SuppressWarnings("unchecked")
	private Collection<Bundle> getTopLevelBundles(Bundle... bundles) {
		Collection<Bundle> topLevelBundles = new LinkedList<Bundle>();
		BundleDescription[] bundleDescriptions = new BundleDescription[bundles.length];
		Map<String, BundleDescription>[] dependencies = new Map[bundles.length];
		int i = 0;
		for(; i < bundles.length; i++) {
			BundleDescription bundleDescription = (BundleDescription) bundles[i].adapt(BundleRevision.class);
			if(bundleDescription == null) {
				throw new RuntimeException("Unable to resolve bundle description for bundle '" + bundles[i].getSymbolicName() + "'.");
			}
			bundleDescriptions[i] = bundleDescription;
			dependencies[i] = BundleDependencies.resolve(true, bundleDescription);
		}
		int j;
		for(i = 0; i < bundles.length; i++) {
			for(j = 0; j < bundles.length; j++) {
				if (i != j) {
					if(dependencies[j].containsKey(bundleDescriptions[i].getSymbolicName())) {
						break;
					}
				}
			}
			if(j == bundles.length) {
				topLevelBundles.add(bundles[i]);
			}
		}
		return topLevelBundles;
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
