package org.mariella.rcp.databinding;

public interface VBindingContextObserver {

void aboutToUpdateModelToTarget();

void aboutToDispose();

void finishedUpdateModelToTarget();

}
