package org.mariella.cat.common;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

public class PropertyChangeUtils {

	public static boolean addPropertyChangeListener(Object receiver, PropertyChangeListener listener) {
		try {
			Method method = receiver.getClass().getMethod("addPropertyChangeListener", new Class<?>[] { PropertyChangeListener.class });
			method.invoke(receiver, listener);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean removePropertyChangeListener(Object receiver, PropertyChangeListener listener) {
		try {
			Method method = receiver.getClass().getMethod("removePropertyChangeListener", new Class<?>[] { PropertyChangeListener.class });
			method.invoke(receiver, listener);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
