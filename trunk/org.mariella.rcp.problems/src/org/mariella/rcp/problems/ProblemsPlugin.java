package org.mariella.rcp.problems;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.logging.JdkLogConfigurator;
import org.mariella.rcp.resources.VResourcesPlugin;
import org.osgi.framework.BundleContext;


public class ProblemsPlugin extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp.problems";

private static ProblemsPlugin plugin;
private static ProblemManager problemManager;
private static JdkLogConfigurator jdkLogConfigurator;

public ProblemsPlugin() {
}

public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	problemManager = new ProblemManager();
	VResourcesPlugin.getResourcePool().addResourceChangeListener(problemManager);
	jdkLogConfigurator = new JdkLogConfigurator(getBundle());
}

public void stop(BundleContext context) throws Exception {
	super.stop(context);
	plugin = null;
	VResourcesPlugin.getResourcePool().removeResourceChangeListener(problemManager);
	problemManager.close();
	problemManager = null;
}

public static ProblemsPlugin getDefault() {
	return plugin;
}

public static void initializeForWindow(IWorkbenchWindow window) {
	VResourcesPlugin.getResourceSelectionManager(window).addSelectionListener(problemManager);
}

public static ProblemManager getProblemManager() {
	return problemManager;
}

public static JdkLogConfigurator getJdkLogConfigurator() {
	return jdkLogConfigurator;
}
}
