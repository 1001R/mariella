package org.mariella.rcp.databinding;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class VDataBindingPlugin extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp.databinding";

public static final Logger logger = Logger.getLogger(PLUGIN_ID);

private static VDataBindingPlugin plugin;

public VDataBindingPlugin() {
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

public static VDataBindingPlugin getDefault() {
	return plugin;
}

}
