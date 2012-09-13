package org.mariella.rcp.databinding;

import org.mariella.rcp.databinding.internal.PropertyPathSupport;

public class BeanUtil {
	
public static Object getValue(Object target, String propertyPath) {
	PropertyPathSupport sup = new PropertyPathSupport();
	sup.object = target;
	sup.propertyPath = propertyPath;
	sup.initialize();
	return sup.implementDoGetValue();
}

public static void setValue(Object target, String propertyPath, Object value) {
	PropertyPathSupport sup = new PropertyPathSupport();
	sup.object = target;
	sup.propertyPath = propertyPath;
	sup.initialize();
	sup.implementDoSetValue(value);
}

}
