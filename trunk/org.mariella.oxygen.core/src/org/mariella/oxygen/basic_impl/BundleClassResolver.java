package org.mariella.oxygen.basic_impl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.osgi.framework.Bundle;

public class BundleClassResolver implements ClassResolver {

	private List<Bundle> bundles;
	
	public BundleClassResolver(List<String> bundleIds) {
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
	
	private List<Bundle> getTopLevelBundles(Bundle... bundles) {
		
		// TODO: FIND OUT TOP LEVEL BUNDLES!!!
//		StateHelper stateHelper = Platform.getPlatformAdmin().getStateHelper();
//		BundleDescription[] descriptions = new BundleDescription[bundles.length];
//		for(int i = bundles.length - 1, j = 0; i >= 0; i--, j++) {
//			BundleDescription bundleDescription = (BundleDescription) bundles[i].adapt(BundleRevision.class);
//			if(bundleDescription == null) {
//				throw new RuntimeException("Unable to resolve bundle description for bundle '" + bundles[i].getSymbolicName() + "'.");
//			}
//			BundleDescription[] dependentBundles = stateHelper.getDependentBundles(new BundleDescription[] { bundleDescription });
//			descriptions[j] = bundleDescription;
//		}
//		Object[][] sorted = stateHelper.sortBundles(descriptions);
		
		return Arrays.asList(bundles);
	}

	public Class<?> resolveClass(String className) throws ClassNotFoundException {
		for (Bundle bundle : bundles) {
			try {
				return bundle.loadClass(className);
			} catch (ClassNotFoundException ex) {
				// continue
			}
		}
		throw new ClassNotFoundException("Class '" + className + "' not resolved from persistence bundles.");
	}

}
