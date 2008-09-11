package org.mariella.glue.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.mariella.rcp.databinding.VBindingContext;
import org.mariella.rcp.forms.ControlFactory;

public abstract class SearchCompositeDecorator <T> {
	protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	protected final VBindingContext bindingContext;
	protected final ControlFactory controlFactory;
	protected final Composite composite;
	protected T searchParameter;
	
public SearchCompositeDecorator(Composite composite, VBindingContext bindingContext, ControlFactory controlFactory) {
	super();
	this.composite = composite;
	this.bindingContext = bindingContext;
	this.controlFactory = controlFactory;
}

public T getSearchParameter() {
	return searchParameter;
}

public void setSearchParameter(T searchParameter) {
	T old  = this.searchParameter;
	this.searchParameter = searchParameter;
	propertyChangeSupport.firePropertyChange("searchParameter", old, searchParameter);
	bindingContext.updateTargets();
}


public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}

public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}

public abstract void decorateComposite();

protected void createSeparator() {
	Label label = controlFactory.createSeparator(composite, SWT.HORIZONTAL);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 4;
	gd.verticalIndent = 10;
	gd.verticalAlignment = SWT.CENTER;
	label.setLayoutData(gd);
	

}
}
