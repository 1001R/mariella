package org.mariella.persistence.annotations.processing;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class AnnotationsPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.mariella.persistence.annotations";

	private static AnnotationsPlugin plugin;
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static AnnotationsPlugin getDefault() {
		return plugin;
	}

}
