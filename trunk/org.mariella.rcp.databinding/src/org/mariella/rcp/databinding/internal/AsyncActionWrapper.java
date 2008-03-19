package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * Wraps an existing action in a way that
 * the targetAction is invoked with Display#asyncExec(...)
 * 
 *  This is needed because of problems refering to the event
 *  order when the user select buttons by using the accelerator.
 *  (focusLost(...) is not properly processed)
 * 
 * @author maschmid
 *
 */
public class AsyncActionWrapper extends Action implements IWorkbenchAction {

Action targetAction;

private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
    public void propertyChange(PropertyChangeEvent event) {
    	AsyncActionWrapper.this.propagateChange(event);
    }
};


public AsyncActionWrapper(Action targetAction) {
	super();
	this.targetAction = targetAction;
	this.targetAction.addPropertyChangeListener(propertyChangeListener);
}

protected void propagateChange(PropertyChangeEvent event) {
    if (event.getProperty().equals(IAction.ENABLED)) {
        Boolean bool = (Boolean) event.getNewValue();
        setEnabled(bool.booleanValue());
    } else if (event.getProperty().equals(IAction.CHECKED)) {
        Boolean bool = (Boolean) event.getNewValue();
        setChecked(bool.booleanValue());
    }
}

public void run() {
	Display.getCurrent().asyncExec(new Runnable() {
		public void run() {
			if (!targetAction.isEnabled())
				throw new IllegalStateException("Target action is not enabled");
			targetAction.run();
		}
	});
}

public void runWithEvent(final Event event) {
	run();
}

public void dispose() {
	this.targetAction.removePropertyChangeListener(propertyChangeListener);
}

}
