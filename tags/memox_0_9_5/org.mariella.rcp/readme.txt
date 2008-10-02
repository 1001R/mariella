To enable log with Jakarta Commons Logging:
=============
Modify MANIFEST.MF:
--------------------------
Add org.mariella.logging to MANIFEST.MF#Require-Bundle section
Add org.apache.commons.logging to MANIFEST.MF#Import-Package section

Provide a static-variable of type JdkLoggingConfigurator to your plugin, for example:
--------------------------
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mariella.logging.JdkLogConfigurator;
import org.osgi.framework.BundleContext;

public class Activator extends AbstractUIPlugin {

public static final String PLUGIN_ID = "org.mariella.rcp";

private static Activator plugin;
private static JdkLogConfigurator jdkLogConfigurator;

public Activator() {
}

public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;

	jdkLogConfigurator = new JdkLogConfigurator(getBundle());
}

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


To configure Logging for your RCP-Application (Client):
=================================
Also modify MANIFEST.MF like described above.
-----------------------------

Add a file (for example) named logging.properties to your RCP root folder.
-----------------------------

For each plugin you want to configure logging:
-----------------------------
In your Activator.start(...) method add the following line of code, for example:

org.mariella.rcp.Activator.getJdkLogConfigurator().configure(
	"logging.properties",	// the logging configuration file 
	Activator.getDefault().getBundle());	// the bundle which contains the logging configuration file
