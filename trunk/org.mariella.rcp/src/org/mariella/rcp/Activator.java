package org.mariella.rcp;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp";

private static Activator plugin;

public static final Logger logger = Logger.getLogger(PLUGIN_ID);


public Activator() {
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

public static Activator getDefault() {
	return plugin;
}

}
