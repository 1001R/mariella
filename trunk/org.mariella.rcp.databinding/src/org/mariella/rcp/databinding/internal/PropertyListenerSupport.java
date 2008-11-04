package org.mariella.rcp.databinding.internal;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PropertyListenerSupport {

private Set elementsListenedTo = new HashSet();

private PropertyChangeListener listener;

private String propertyName;

public Object userObject;


public PropertyListenerSupport(final PropertyChangeListener listener,
		final String propertyName) {
	Assert.isNotNull(listener);
	Assert.isNotNull(propertyName);

	this.propertyName = propertyName;
	this.listener = new PropertyChangeListener() {
		public void propertyChange(final PropertyChangeEvent evt) {
			if (Realm.getDefault() == null)
				throw new IllegalStateException("No Realm available (Realm.getDefault() == null)");
			Realm.getDefault().exec(new Runnable() {
				@Override
				public void run() {
					if (propertyName.equals(evt.getPropertyName())) {
						listener.propertyChange(evt);
					}
				}
			});
		}
	};
}

/**
 * Start listen to target (if it supports the JavaBean property change listener pattern)
 * 
 * @param target
 */
public void hookListener(Object target) {
	if (target != null) {
		if (processListener(
				"addPropertyChangeListener", target)) { //$NON-NLS-1$ 
			elementsListenedTo.add(new IdentityWrapper(target));
		}
	}
}
	
/**
 * Add listeners for new targets (those this instance of<code>ListenerSupport</code> does not 
 * already listen to),
 * Stop to listen to those object that this instance listen to and is one of the object in targets 
 * 
 * @param targets 
 */
public void setHookTargets(Object[] targets) {		
	Set elementsToUnhook = new HashSet(elementsListenedTo);
	if (targets!=null) {
		for (int i = 0; i < targets.length; i++) {
			Object newValue = targets[i];
			IdentityWrapper identityWrapper = new IdentityWrapper(newValue);
			if(!elementsToUnhook.remove(identityWrapper)) 				
				hookListener(newValue);
		}
	}
		
	for (Iterator it = elementsToUnhook.iterator(); it.hasNext();) {
		Object o = it.next();
		if (o.getClass()!=IdentityWrapper.class)
			o = new IdentityWrapper(o);
		elementsListenedTo.remove(o);
		unhookListener(o);
	}							
}

/**
 * Stop listen to target
 * 
 * @param target
 */
public void unhookListener(Object target) {
	if (target.getClass() == IdentityWrapper.class)
		target = ((IdentityWrapper) target).unwrap();

	if (processListener(
			"removePropertyChangeListener", target)) { //$NON-NLS-1$
		elementsListenedTo.remove(new IdentityWrapper(target));
	}
}


/**
 * 
 */
public void dispose() {
	if (elementsListenedTo!=null) {
		Object[] targets = elementsListenedTo.toArray();		
		for (int i = 0; i < targets.length; i++) {		
			unhookListener(targets[i]);
		}			
		elementsListenedTo=null;
		listener=null;
	}
}

/**
 * @return elements that were registred to
 */
public Object[] getHookedTargets() {
	Object[] targets = null;
	if (elementsListenedTo!=null && elementsListenedTo.size()>0) {
		Object[] identityList = elementsListenedTo.toArray();
		targets = new Object[identityList.length];
		for (int i = 0; i < identityList.length; i++) 
			targets[i]=((IdentityWrapper)identityList[i]).unwrap();							
	}
	return targets;
}

/**
 * Invokes the method for the provided <code>methodName</code> attempting
 * to first use the method with the property name and then the unnamed
 * version.
 * 
 * @param methodName
 *            either addPropertyChangeListener or
 *            removePropertyChangeListener
 * @param message
 *            string that will be prefixed to the target in an error message
 * @param target
 *            object to invoke the method on
 * @return <code>true</code> if the method was invoked successfully
 */
private boolean processListener(String methodName, Object target) {
	Method method = null;
	Object[] parameters = null;

	try {
		try {
			method = target.getClass().getMethod(
					methodName,
					new Class[] { String.class,
							PropertyChangeListener.class });

			parameters = new Object[] { propertyName, listener };
		} catch (NoSuchMethodException e) {
			method = target.getClass().getMethod(methodName,
					new Class[] { PropertyChangeListener.class });

			parameters = new Object[] { listener };
		}
	} catch (SecurityException e) {
		// ignore
	} catch (NoSuchMethodException e) {
		// ignore too
	}

	if (method != null) {
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		try {
			method.invoke(target, parameters);
			return true;
		} catch (IllegalArgumentException e) {
			log(IStatus.WARNING, "Error during invocation of " + method + " on target " + target, e);
		} catch (IllegalAccessException e) {
			log(IStatus.WARNING, "Error during invocation of " + method + " on target " + target, e);
		} catch (InvocationTargetException e) {
			log(IStatus.WARNING, "Error during invocation of " + method + " on target " + target, e);
		}
	}
	return false;
}

/**
 * Logs a message to the Data Binding logger.
 */
private void log(int severity, String message, Throwable throwable) {
	if (BeansObservables.DEBUG) {
		Policy.getLog().log(
				new Status(severity, Policy.JFACE_DATABINDING, IStatus.OK,
						message, throwable));
	}
}

}
