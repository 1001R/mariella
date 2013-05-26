package org.mariella.oxygen.basic_impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.service.resolver.BaseDescription;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.osgi.service.resolver.BundleSpecification;
import org.eclipse.osgi.service.resolver.ExportPackageDescription;
import org.eclipse.osgi.service.resolver.HostSpecification;
import org.eclipse.osgi.service.resolver.ImportPackageSpecification;
import org.osgi.framework.Constants;


public class BundleDependencies {

	public static Map<String, BundleDescription> resolve(boolean includeOptional, BundleDescription... bundleDescriptions) {
		Map<String, BundleDescription> dependencies = new HashMap<String, BundleDescription>();
		for (int i = 0; i < bundleDescriptions.length; i++) {
			collectDependencies(dependencies, bundleDescriptions[i], includeOptional);
		}
		return dependencies;
	}

	private static void collectDependencies(Map<String, BundleDescription> dependencies, BundleDescription bundleDescription, boolean includeOptional) {
		if (bundleDescription == null) {
			return;
		}
		String id = bundleDescription.getSymbolicName();
		if (dependencies.containsKey(id)) {
			return;
		}
		dependencies.put(id, bundleDescription);

		collectRequiredBundles(dependencies, bundleDescription.getRequiredBundles(), includeOptional);
		collectImportedPackages(dependencies, bundleDescription.getImportPackages(), includeOptional);

		HostSpecification host = bundleDescription.getHost();
		if (host != null) {
			// if current bundle is a fragment -> include host bundle
			BaseDescription supplier = host.getSupplier();
			if (supplier != null && supplier instanceof BundleDescription) {
				collectDependencies(dependencies, (BundleDescription) supplier, includeOptional);
			}
		} else {
			// otherwise, include applicable fragments for bundle
			collectFragments(dependencies, bundleDescription, includeOptional);
		}
	}

	private static void collectRequiredBundles(Map<String, BundleDescription> dependencies, BundleSpecification[] requiredBundles, boolean includeOptional) {
		for (int i = 0; i < requiredBundles.length; i++) {
			if (requiredBundles[i].isOptional() && !includeOptional) {
				continue;
			}
			BaseDescription supplier = requiredBundles[i].getSupplier();
			// only recursively search statisfied require-bundles
			if (supplier != null && supplier instanceof BundleDescription) {
				collectDependencies(dependencies, (BundleDescription) supplier, includeOptional);
			}
		}
	}

	private static void collectImportedPackages(Map<String, BundleDescription> dependencies, ImportPackageSpecification[] packages, boolean includeOptional) {
		for (int i = 0; i < packages.length; i++) {
			if (!includeOptional) {
				if (Constants.RESOLUTION_OPTIONAL.equals(packages[i].getDirective(Constants.RESOLUTION_DIRECTIVE))) {
					continue;
				}
			}
			BaseDescription supplier = packages[i].getSupplier();
			// only recursively search statisfied import-packages
			if (supplier != null && supplier instanceof ExportPackageDescription) {
				BundleDescription exporter = ((ExportPackageDescription) supplier).getExporter();
				if (exporter != null) {
					collectDependencies(dependencies, exporter, includeOptional);
				}
			}
		}
	}

	private static void collectFragments(Map<String, BundleDescription> dependencies, BundleDescription host, boolean includeOptional) {
		BundleDescription[] fragments = host.getFragments();
		for (int i = 0; i < fragments.length; i++) {
			if (fragments[i].isResolved() && !fragments[i].getSymbolicName().equals("org.eclipse.ui.workbench.compatibility")) {
				collectDependencies(dependencies, fragments[i], includeOptional);
			}
		}
	}
	
}
