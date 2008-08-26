package org.mariella.sample.app;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.rcp.resources.VResourcesPlugin;
import org.mariella.sample.app.person.PersonResourceManager;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.sample.app";

private static Activator plugin;

public Activator() {
}

public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;

	// register mariella resource managers
	VResourcesPlugin.getResourceManagerRegistry().addManager(new PersonResourceManager());
}

public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
}

public static Activator getDefault() {
	return plugin;
}

public static ImageDescriptor getImageDescriptor(String path) {
	return imageDescriptorFromPlugin(PLUGIN_ID, path);
}
}
