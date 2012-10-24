package org.mariella.oxygen.basic_impl;

import org.eclipse.core.runtime.Platform;
import org.mariella.oxygen.basic_core.ClassResolver;
import org.osgi.framework.Bundle;

public class BundleClassResolver implements ClassResolver {

	private final String bundleName;
	private final String bundleVersion;

	public BundleClassResolver(String bundleName) {
		this(bundleName, null);
	}

	public BundleClassResolver(String bundleName, String bundleVersion) {
		this.bundleName = bundleName;
		this.bundleVersion = bundleVersion;
	}

	public String getBundleName() {
		return bundleName;
	}

	public String getBundleVersion() {
		return bundleVersion;
	}

	public Bundle resolveBundle() {
		Bundle[] bundles = Platform.getBundles(bundleName, bundleVersion);
		if(bundles != null && bundles.length > 0) {
			return bundles[0];
		}
		return null;
	}

	public Class<?> resolveClass(String className) throws ClassNotFoundException {
		Bundle bundle = resolveBundle();
		if(bundle == null) {
			throw new RuntimeException("Bundle '" + getBundleName() +
					"' can't be resolved to load class '" + className + "'.");
		}
		return bundle.loadClass(className);
	}

}
