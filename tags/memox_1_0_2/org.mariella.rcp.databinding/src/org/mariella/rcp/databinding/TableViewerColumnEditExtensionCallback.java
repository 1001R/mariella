package org.mariella.rcp.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public interface TableViewerColumnEditExtensionCallback {

Control createEditControl(IObservableValue selectionHolder, Composite parent);

}
