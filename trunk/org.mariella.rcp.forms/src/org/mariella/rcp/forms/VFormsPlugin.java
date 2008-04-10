package org.mariella.rcp.forms;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.logging.JdkLogConfigurator;
import org.osgi.framework.BundleContext;

public class VFormsPlugin extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp.forms";
private static VFormsPlugin plugin;
private static JdkLogConfigurator jdkLogConfigurator;

public VFormsPlugin() {
}

@Override
public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	jdkLogConfigurator = new JdkLogConfigurator(getBundle());
}

@Override
public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
}

public static VFormsPlugin getDefault() {
	return plugin;
}

public static JdkLogConfigurator getJdkLogConfigurator() {
	return jdkLogConfigurator;
}

}
