package org.mariella.rcp.databinding;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.swt.widgets.Control;

public abstract class SWTObservableStatusDecorator {

protected  abstract void initializeFor(Control control);

public abstract void decorateStatus(ISWTObservable observable, IStatus validationStatus);

}
