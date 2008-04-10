package org.mariella.logging;

import java.io.IOException;
import java.net.URL;
import java.util.logging.LogManager;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

public class JdkLogConfigurator {
	
LogManager logManager;
Bundle bundle;

public JdkLogConfigurator(Bundle bundle) {
	this.bundle = bundle;
	this.logManager = LogManager.getLogManager();
	System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
}

public void configure(String logfile, Bundle clientBundle) throws SecurityException, IOException {
	URL fullPath = FileLocator.findEntries(clientBundle, new Path(logfile))[0];
	logManager.readConfiguration(fullPath.openConnection().getInputStream());
}

}
