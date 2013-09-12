package org.mariella.oxygen.remoting.common;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.mariella.oxygen.basic_impl.BundleClassResolver;
import org.osgi.framework.Bundle;

public class RemotingClassResolver extends BundleClassResolver {

public RemotingClassResolver() {
	super();
	IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.mariella.oxygen.remoting.remotableBundles");
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
}

