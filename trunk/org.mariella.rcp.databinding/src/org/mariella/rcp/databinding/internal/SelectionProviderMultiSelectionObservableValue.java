package org.mariella.rcp.databinding.internal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionProviderMultiSelectionObservableValue extends AbstractObservableValue {

private final ISelectionProvider selectionProvider;
private boolean updating = false;
private Object currentSelection;
private ISelectionChangedListener selectionChangedListener;

public SelectionProviderMultiSelectionObservableValue(ISelectionProvider selectionProvider) {
	super();
	if (selectionProvider == null) {
		throw new IllegalArgumentException(
				"The 'selectionProvider' parameter is null."); //$NON-NLS-1$
	}

	this.selectionProvider = selectionProvider;
	this.currentSelection = doGetValue();

	selectionChangedListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (!updating) {
				Object oldSelection = currentSelection;
				currentSelection = doGetValue();
				fireValueChange(Diffs.createValueDiff(oldSelection,
						currentSelection));
			}
		}
	};
	selectionProvider.addSelectionChangedListener(selectionChangedListener);
}

public void doSetValue(final Object value) {
	try {
		updating = true;

		Object oldSelection = currentSelection;
		selectionProvider
				.setSelection(value == null ? StructuredSelection.EMPTY
						: new StructuredSelection((List)value));
		currentSelection = doGetValue();
		if (!Util.equals(oldSelection == null ? new Object[0] : ((List)oldSelection).toArray(), currentSelection == null ? new Object[0] : ((List)currentSelection).toArray())) {
			fireValueChange(Diffs.createValueDiff(oldSelection,
					currentSelection));
		}
	} finally {
		updating = false;
	}
}

protected Object doGetValue() {
	ISelection selection = selectionProvider.getSelection();
	if (selection instanceof IStructuredSelection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		return Arrays.asList(sel.toArray());
	}

	return null;
}

public Object getValueType() {
	return null;
}

/*
 * (non-Javadoc)
 *
 * @see org.eclipse.core.databinding.observable.value.AbstractObservableValue#dispose()
 */
public synchronized void dispose() {
	selectionProvider
			.removeSelectionChangedListener(selectionChangedListener);
	super.dispose();
}

}
