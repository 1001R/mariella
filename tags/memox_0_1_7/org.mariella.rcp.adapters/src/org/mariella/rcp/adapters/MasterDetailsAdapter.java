package org.mariella.rcp.adapters;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;

public abstract class MasterDetailsAdapter<K extends Object, D extends Object> extends AbstractAdapter {

	private D selectedDetails;
	private final List<D> detailsList;
	private IObservableValue selectedDetailsObservable;
	
@SuppressWarnings("unchecked")
public MasterDetailsAdapter(AdapterContext context) {
	super(context);
	detailsList = context.getBindingContext().getBindingFactory().createObservableList(context.getBindingContext());
	selectedDetailsObservable = context.getBindingContext().getBindingFactory().createPropertyObservable(
			context.getBindingContext(), 
			this, "selectedDetails"); //$NON-NLS-1$
}

public K getSelectedDetailsKey() {
	if (selectedDetails == null) return null;
	return getDetailsKey(selectedDetails);
}
	
public void setSelectedDetailsKey(K key) {
	Object oldKey = getSelectedDetailsKey();
	Object oldDetails = selectedDetails;
	
	if (key == null) {
		selectedDetails = null;
	} else {
		D details = getDetails(key);
		if (details != null) {
			selectedDetails = details;
		} else {
			selectedDetails = createDetails(key);
		}
		
		if (autoManageDetailsList() && !isInDetailsList(key)) {
			addSelectedDetailsToList();
			getAdapterContext().adapterDirtyNotification(this);
		}
	}	
	
	propertyChangeSupport.firePropertyChange("selectedDetailsKey", oldKey, key); //$NON-NLS-1$
	propertyChangeSupport.firePropertyChange("selectedDetails", oldDetails, selectedDetails); //$NON-NLS-1$
}

protected boolean autoManageDetailsList() {
	return true;
}

public void addSelectedDetailsToList() {
	aboutToAddDetails(selectedDetails);
	if (revertDetails())
		detailsList.add(0, selectedDetails);
	else
		detailsList.add(selectedDetails);
}

public void removeSelectedDetailsFromList() {
	removeDetails(selectedDetails);
}

protected boolean isInDetailsList(K key) {
	for (D d : detailsList)
		if (matchesKey(getDetailsKey(d), key))
			return true;
	return false;
}

protected boolean revertDetails() {
	return true;
}

private D getDetails(K key) {
	for (D details : detailsList) {
		if (matchesKey(getDetailsKey(details), key))
			return details;
	}
	return null;
}

protected abstract boolean matchesKey(K key1, K key2);

protected abstract K getDetailsKey(D details);

protected abstract D createDetails(K key);

protected void aboutToAddDetails(D details) {}

protected void aboutToRemoveDetails(D details) {}

public D getSelectedDetails() {
	return selectedDetails;
}

public IObservableValue getSelectedDetailsObservable() {
	return selectedDetailsObservable;
}

public void removeDetails(D details) {
	aboutToRemoveDetails(details);
	detailsList.remove(details);
	if (selectedDetails == details && autoManageDetailsList())
		setSelectedDetailsKey(null);
	getAdapterContext().adapterDirtyNotification(this);
}

public List<D> getDetailsList() {
	return detailsList;
}

public void clearDetailsList() {
	detailsList.clear();
	if (autoManageDetailsList())
		setSelectedDetailsKey(null);
	adapterContext.adapterDirtyNotification(this);
}
}
