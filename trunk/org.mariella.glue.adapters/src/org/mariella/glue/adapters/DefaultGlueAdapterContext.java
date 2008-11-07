package org.mariella.glue.adapters;

import org.mariella.glue.service.Context;
import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.runtime.ModificationTrackerListener;
import org.mariella.rcp.adapters.DefaultAdapterContext;
import org.mariella.rcp.databinding.VBindingContext;

public class DefaultGlueAdapterContext extends DefaultAdapterContext implements	GlueAdapterContext, ModificationTrackerListener  {

	Context glueContext = null;
	
public DefaultGlueAdapterContext(VBindingContext bindingContext, Context glueContext) {
	super(bindingContext);
	setGlueContext(glueContext);
}

public Context getGlueContext() {
	return glueContext;
}

public void setGlueContext(Context glueContext) {
	if (this.glueContext != null)
		this.glueContext.getModificationTracker().removeListener(this);
	this.glueContext = glueContext;
	if (this.glueContext != null)
		this.glueContext.getModificationTracker().addListener(this);
}

@Override
public void indexedPropertyChanged(Modifiable modifiable, String propertyName, int index, Object oldValue, Object newValue) {
	dirtyNotification(modifiable);
}

@Override
public void propertyChanged(Modifiable modifiable, String propertyName, Object oldValue, Object newValue) {
	dirtyNotification(modifiable);
}

}
