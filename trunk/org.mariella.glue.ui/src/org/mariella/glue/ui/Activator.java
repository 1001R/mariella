package org.mariella.glue.ui;

import java.util.logging.Logger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.bia.common.logging.api.Logging;

public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.mariella.glue.ui";
	private static Activator plugin;
	public static final Logger logger = Logger.getLogger(PLUGIN_ID);
	
public Activator() {
}

public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	Logging.Singleton.register(logger.getName());
}

public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
}

public static Activator getDefault() {
	return plugin;
}

}
