package org.mariella.rcp.forms;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VFormsPlugin extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp.forms";

public static final Logger logger = Logger.getLogger(PLUGIN_ID);

private static VFormsPlugin plugin;

public VFormsPlugin() {
}

@Override
public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
}

@Override
public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
}

public static VFormsPlugin getDefault() {
	return plugin;
}

}
