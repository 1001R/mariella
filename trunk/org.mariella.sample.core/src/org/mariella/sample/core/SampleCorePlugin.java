package org.mariella.sample.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class SampleCorePlugin extends Plugin {

public static final String PLUGIN_ID = "org.mariella.sample.core";

private static SampleCorePlugin plugin;
private static SampleCoreService coreService;

public SampleCorePlugin() {
}

public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	coreService = new SampleCoreService();
}

public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
	coreService = null;
}

public static SampleCorePlugin getDefault() {
	return plugin;
}

public static SampleCoreService getCoreService() {
	return coreService;
}

}
