package org.mariella.rcp.adapters;

import java.util.ArrayList;
import java.util.List;

import org.mariella.rcp.databinding.VBindingContext;

public class DefaultAdapterContext extends AbstractAdapterContext {

	private List<AdapterContextObserver> observers = new ArrayList<AdapterContextObserver>();
	
public DefaultAdapterContext(VBindingContext bindingContext) {
	super(bindingContext);
}

@Override
public void dirtyNotification(Object source) {}

@Override
public void addObserver(AdapterContextObserver o) {
	observers.add(o);
}

@Override
public void removeObserver(AdapterContextObserver o) {
	observers.remove(o);
}

}
