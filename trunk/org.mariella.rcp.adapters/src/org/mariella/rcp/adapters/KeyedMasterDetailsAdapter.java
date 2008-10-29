package org.mariella.rcp.adapters;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;

/**
 * Useful for input management where you don't have an ecplicit "add" action, instead
 * an item is added to the detailsList by setting the "selectedKey" property.
 * 
 * This is useful where you select/add an item with a scanner. (Like in a shop where
 * the products are scanned and each time an item is added to the list). 
 * 
 * @author maschmid
 *
 * @param <K>
 * @param <D>
 */
public abstract class KeyedMasterDetailsAdapter<K extends Object, D extends Object> extends AbstractAdapter {

private D selectedDetails;

private List<D> detailsList;

private IObservableValue selectedDetailsObservable;

public KeyedMasterDetailsAdapter(AdapterContext context) {
	super(context);
	initialize();
}

public KeyedMasterDetailsAdapter(Adapter parent) {
	super(parent);
	initialize();
}

@SuppressWarnings("unchecked")
private void initialize() {
	detailsList = adapterContext.getBindingContext().getBindingFactory().createObservableList(adapterContext.getBindingContext());
	selectedDetailsObservable = adapterContext.getBindingContext().getBindingFactory().createPropertyObservable(adapterContext.getBindingContext(), this, "selectedDetails"); //$NON-NLS-1$
}

public K getSelectedKey() {
	if (selectedDetails == null)
		return null;
	return getKey(selectedDetails);
}

public void setSelectedKey(K key) {
	Object oldKey = getSelectedKey();
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
			fireAdapterDirty();
		}
	}

	firePropertyChange("selectedKey", oldKey, key); //$NON-NLS-1$
	firePropertyChange("selectedDetails", oldDetails, selectedDetails); //$NON-NLS-1$
}

protected boolean autoManageDetailsList() {
	return true;
}

public void addSelectedDetailsToList() {
	if (selectedDetails != null)
		addDetails(selectedDetails);
}

public void addDetails(D details) {
	aboutToAddDetails(details);
	if (revertDetails())
		detailsList.add(0, details);
	else
		detailsList.add(details);
	addedDetails(details);
}

public void removeSelectedDetailsFromList() {
	removeDetails(selectedDetails);
}

protected boolean isInDetailsList(K key) {
	for (D d : detailsList)
		if (matchesKey(getKey(d), key))
			return true;
	return false;
}

protected boolean revertDetails() {
	return true;
}

public D getDetails(K key) {
	for (D details : detailsList) {
		if (matchesKey(getKey(details), key))
			return details;
	}
	return null;
}

protected abstract boolean matchesKey(K key1, K key2);

protected abstract K getKey(D details);

protected abstract D createDetails(K key);

protected void aboutToAddDetails(D details) {
}

protected void aboutToRemoveDetails(D details) {
}

protected void addedDetails(D details) {
}

protected void removedDetails(D details) {
}
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
		setSelectedKey(null);
	getAdapterContext().adapterDirtyNotification(this);
	removedDetails(details);
}

public List<D> getDetailsList() {
	return detailsList;
}

public void clearDetailsList() {
	detailsList.clear();
	if (autoManageDetailsList())
		setSelectedKey(null);
	adapterContext.adapterDirtyNotification(this);
}
}
