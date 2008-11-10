package org.mariella.glue.adapters;

import org.mariella.glue.service.Context;
import org.mariella.rcp.adapters.AdapterContext;

public interface GlueAdapterContext extends AdapterContext {

public Context getGlueContext();

public void resetGlueContext();
	
}
