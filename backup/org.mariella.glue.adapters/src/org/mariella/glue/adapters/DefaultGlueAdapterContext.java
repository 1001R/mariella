package org.mariella.glue.adapters;

import org.mariella.glue.service.Context;
import org.mariella.glue.service.ContextFactory;
import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.runtime.ModificationTrackerListener;
import org.mariella.rcp.adapters.DefaultAdapterContext;
import org.mariella.rcp.databinding.VBindingContext;

public class DefaultGlueAdapterContext extends DefaultAdapterContext implements	GlueAdapterContext, ModificationTrackerListener  {

	ContextFactory glueContextFactory;
	Context glueContext = null;
	boolean autoManageDirtyNotification;
	
public DefaultGlueAdapterContext(VBindingContext bindingContext, ContextFactory glueContextFactory) {
	this(bindingContext, glueContextFactory, true);
}

public DefaultGlueAdapterContext(VBindingContext bindingContext, ContextFactory glueContextFactory, boolean autoManageDirtyNotification) {
	super(bindingContext);
	this.glueContextFactory = glueContextFactory;
	setGlueContext(glueContextFactory.createGlueContext());
	this.autoManageDirtyNotification = autoManageDirtyNotification;
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
	if (autoManageDirtyNotification)
		dirtyNotification(modifiable);
}

@Override
public void propertyChanged(Modifiable modifiable, String propertyName, Object oldValue, Object newValue) {
	if (autoManageDirtyNotification)
		dirtyNotification(modifiable);
}

@Override
public void resetGlueContext() {
	setGlueContext(glueContextFactory.createGlueContext());
}

}
