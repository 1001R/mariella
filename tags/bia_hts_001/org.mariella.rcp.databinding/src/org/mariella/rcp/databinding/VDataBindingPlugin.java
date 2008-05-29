package org.mariella.rcp.databinding;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.logging.JdkLogConfigurator;
import org.osgi.framework.BundleContext;

public class VDataBindingPlugin extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp.databinding";

private static VDataBindingPlugin plugin;
private static JdkLogConfigurator jdkLogConfigurator;

public VDataBindingPlugin() {
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

public static VDataBindingPlugin getDefault() {
	return plugin;
}

public static JdkLogConfigurator getJdkLogConfigurator() {
	return jdkLogConfigurator;
}

}
