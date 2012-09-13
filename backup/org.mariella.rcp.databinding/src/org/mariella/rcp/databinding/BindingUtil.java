package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.PropertyPathSupport;

public class BindingUtil {

public static void setValue(Object objectOrObservable, String propertyPath, Object value) {
	PropertyPathSupport sup = new PropertyPathSupport();
	sup.object = objectOrObservable;
	sup.propertyPath = propertyPath;
	sup.initialize();
	sup.implementDoSetValue(value);
}

public static Object getValue(Object objectOrObservable, String propertyPath) {
	PropertyPathSupport sup = new PropertyPathSupport();
	sup.object = objectOrObservable;
	sup.propertyPath = propertyPath;
	sup.initialize();
	return sup.implementDoGetValue();
}

}
