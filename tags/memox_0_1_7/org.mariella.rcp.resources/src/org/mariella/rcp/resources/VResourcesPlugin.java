package org.mariella.rcp.resources;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VResourcesPlugin extends AbstractUIPlugin {

// The plug-in ID
public static final String PLUGIN_ID = "org.mariella.rcp.vresources";

public static final Logger logger = Logger.getLogger(PLUGIN_ID);

private static VResourcesPlugin plugin;
private static VResourcePool resourcePool;
private static VResourceManagerRegistry resourceManagerRegistry;
private static Map<IWorkbenchWindow,VResourceSelectionManager> resouceSelectionManagerMap = new HashMap<IWorkbenchWindow, VResourceSelectionManager>();

public VResourcesPlugin() {
}

@Override
public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	resourcePool = new VResourcePool();
	resourceManagerRegistry = new VResourceManagerRegistry();
}

@Override
public void stop(BundleContext context) throws Exception {
	super.stop(context);
	plugin = null;
	resourcePool.close();
	resourceManagerRegistry.close();
	resourcePool = null;
	resourceManagerRegistry = null;
}

public static VResourcesPlugin getDefault() {
	return plugin;
}

public static VResourcePool getResourcePool() {
	return resourcePool;
}

public static VResourceSelectionManager getResourceSelectionManager(IWorkbenchWindow window) {
	return resouceSelectionManagerMap.get(window);
}

public static void initializeForWindow(IWorkbenchWindow window) {
	resouceSelectionManagerMap.put(window, new VResourceSelectionManager(window));
}

public static VResourceManagerRegistry getResourceManagerRegistry() {
	return resourceManagerRegistry;
}

}
