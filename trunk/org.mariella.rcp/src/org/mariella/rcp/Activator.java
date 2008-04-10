package org.mariella.rcp;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.logging.JdkLogConfigurator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp";

private static Activator plugin;
private static JdkLogConfigurator jdkLogConfigurator;

public Activator() {
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

public static Activator getDefault() {
	return plugin;
}

public static JdkLogConfigurator getJdkLogConfigurator() {
	return jdkLogConfigurator;
}

}
