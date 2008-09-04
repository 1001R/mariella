package org.mariella.rcp.adapters;

import org.mariella.rcp.databinding.VBindingContext;


public interface AdapterContext {

VBindingContext getBindingContext();

void adapterDirtyNotification(Adapter adapter);

void addObserver(AdapterContextObserver o);

void removeObserver(AdapterContextObserver o);

}
