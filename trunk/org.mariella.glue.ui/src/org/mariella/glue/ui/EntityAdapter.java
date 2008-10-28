package org.mariella.glue.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.mariella.glue.service.Context;
import org.mariella.glue.service.Entity;
import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.runtime.ModificationTrackerListener;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.problems.ProblemResource;
import org.mariella.rcp.problems.ProblemResourceHolder;
import org.mariella.rcp.resources.AbstractPersistableVResource;


public abstract class EntityAdapter <T extends Entity> extends AbstractPersistableVResource implements IAdaptable, ProblemResourceHolder, ModificationTrackerListener, IEditorInput {
	protected final T entity;
	protected final Context context;
	protected final VBindingContext dataBindingContext;
	protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
public EntityAdapter(Context context, T entity) {
	super();
	this.entity = entity;
	dataBindingContext = getUIRegistration().getDataBindingFactory().createBindingContext();
	this.context = context;
	context.getModificationTracker().addListener(this);
}

public EntityReference createEntityReference() {
	return new EntityReference(entity.getClass().getName(), entity.getId());
}

@Override
public String getFactoryId() {
	return EntityAdapterElementFactory.ID;
}

@Override
public String getName() {
	return "<unnamed>";
}

public UIRegistration<T> getUIRegistration() {
	return UIRegistry.Singleton.get(getEntity().getClass());
}

public Long getId() {
	return entity.getId();
}

public void reload() {
	getUIRegistration().getService().loadForEditing(context, getId(), true);
	dataBindingContext.updateTargets();
}

public T getEntity() {
	return entity;
}

public Context getContext() {
	return context;
}

public boolean exists() {
	return false;
}

public void propertyChanged(Modifiable modifiable, String propertyName, Object oldValue, Object newValue) {
	fireChanged();
}

public void indexedPropertyChanged(Modifiable modifiable, String propertyName, int index, Object oldValue, Object newValue) {
	fireChanged();
}

@Override
public void dispose() {
	context.getModificationTracker().removeListener(this);
	super.dispose();
	dataBindingContext.dispose();
}

public ImageDescriptor getImageDescriptor() {
	return null;
}

public String getToolTipText() {
	return getName() == null ? "<unnamed>" : getName();
}

@SuppressWarnings("unchecked")
public Object getAdapter(Class adapter) {
	return null;
}

public ProblemResource getProblemResource() {
	return new ScreeningProblemResource(getUIRegistration(), getRef(), getName());
}

public void addPropertyChangeListener(PropertyChangeListener l) {
	propertyChangeSupport.addPropertyChangeListener(l);
}

public void removePropertyChangeListener(PropertyChangeListener l) {
	propertyChangeSupport.removePropertyChangeListener(l);
}

}
