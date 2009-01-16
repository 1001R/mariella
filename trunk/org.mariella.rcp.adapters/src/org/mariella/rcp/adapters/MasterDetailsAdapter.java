package org.mariella.rcp.adapters;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;

public abstract class MasterDetailsAdapter<D extends Object> extends AbstractAdapter {

	private D selectedDetails;
	private List<D> detailsList;
	private IObservableValue selectedDetailsObservable;
	
public MasterDetailsAdapter(AdapterContext context) {
	super(context);
	initialize();
}

public MasterDetailsAdapter(Adapter parent) {
	super(parent);
	initialize();
}

private void initialize() {
	detailsList = fetchDetailsList();
	selectedDetailsObservable = adapterContext.getBindingContext().getBindingFactory().createPropertyObservable(
			adapterContext.getBindingContext(), 
			this, "selectedDetails"); //$NON-NLS-1$

}

@SuppressWarnings("unchecked")
protected List<D> fetchDetailsList() {
	return adapterContext.getBindingContext().getBindingFactory().createObservableList(adapterContext.getBindingContext());
}

public void removeSelectedDetails() {
	if (selectedDetails == null) return;
	
	removeDetails(selectedDetails);
}

protected boolean revertDetails() {
	return false;
}

protected void aboutToAddDetails(D details) {}

protected void aboutToRemoveDetails(D details) {}

public D getSelectedDetails() {
	return selectedDetails;
}

public IObservableValue getSelectedDetailsObservable() {
	return selectedDetailsObservable;
}

public void setSelectedDetails(D details) {
	Object oldDetails = selectedDetails;
	selectedDetails = details;
	firePropertyChange("selectedDetails", oldDetails, selectedDetails); //$NON-NLS-1$
}

public D addDetails() {
	D details = createDetails();
	addDetails(details);
	createdDetails(details);
	return details;
}

protected void createdDetails(D details) {}

protected D createDetails() {
	throw new UnsupportedOperationException();
}

public void addDetails(D details) {
	aboutToAddDetails(details);
	detailsList.add(details);
	addedDetails(details);
	getAdapterContext().dirtyNotification(this);
	setSelectedDetails(details);
}

public void setDetails(Collection<D> details) {
	detailsList.clear();
	detailsList.addAll(details);
	getAdapterContext().dirtyNotification(this);
}

protected void addedDetails(D details) {}

public void removeDetails(D details) {
	aboutToRemoveDetails(details);
	detailsList.remove(details);
	removedDetails(details);
	if (selectedDetails == details)
		setSelectedDetails(null);
	getAdapterContext().dirtyNotification(this);
}

protected void removedDetails(D details) {}

public List<D> getDetailsList() {
	return detailsList;
}

public IObservableList getObservableDetailsList() {
	return (IObservableList)detailsList;
}

public void clearDetailsList() {
	detailsList.clear();
	setSelectedDetails(null);
	adapterContext.dirtyNotification(this);
}

public void setToFirstSelectedDetails() {
	if (detailsList.size() == 0) return;
	setSelectedDetails(detailsList.get(0));
}
}
