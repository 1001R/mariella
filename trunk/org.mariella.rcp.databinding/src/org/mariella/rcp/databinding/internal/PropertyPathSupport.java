package org.mariella.rcp.databinding.internal;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.mariella.rcp.databinding.VDataBindingPlugin;

// TODO Index Brackes are currently not allowed at the end of the path. 
public class PropertyPathSupport {

static class UndefinedPropertyDescriptor extends PropertyDescriptor {
public UndefinedPropertyDescriptor(String propertyName) throws IntrospectionException {
	super(propertyName, null, null);
}
}


public Object object;
public String propertyPath;
List<String> pathTokens;
Map<Class, PropertyDescriptor> cachedPropertyDescriptorMap = new HashMap<Class, PropertyDescriptor>();

public void initialize() {
	StringTokenizer t = new StringTokenizer(propertyPath, ".");
	pathTokens = new ArrayList<String>(t.countTokens());
	while (t.hasMoreTokens())
		pathTokens.add(t.nextToken());
}

public Object implementDoGetValue() {
	try {
		Object targetObject = readTargetObject();
		if (targetObject == null) return null;
		if (propertyPath.equals("this")) return targetObject;
		PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, pathTokens.get(pathTokens.size()-1));
		if (propDescr.getReadMethod() == null) {
			return null;
		}
		return propDescr.getReadMethod().invoke(targetObject, (Object[])null);
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

public void implementDoSetValue(Object value) {
	try {
		Object targetObject = readTargetObject();
		if (targetObject == null) return;
		PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, pathTokens.get(pathTokens.size()-1));
		if (propertyPath.equals("this"))
			throw new IllegalStateException("Path: " + propertyPath + ": A 'this' property cannot be written");
		if (propDescr.getWriteMethod() == null) {
			if (VDataBindingPlugin.logger.isLoggable(Level.FINE))
				VDataBindingPlugin.logger.log(Level.FINE, "Path: " + propertyPath + ": Cannot set value because property " + propDescr.getName() + " has no write method");
			return;
		}
		propDescr.getWriteMethod().invoke(targetObject, value);
	} catch (Exception e) {
		VDataBindingPlugin.logger.severe("Path: " + propertyPath + ": Target object is null (" + "IObservableValue " + object + " returned null)");
		throw new RuntimeException(e);
	}
}

Object readTargetObject() {
	Object element = null;
	if (object instanceof IObservableValue) {
		element = ((IObservableValue)object).getValue();
		if (element == null && VDataBindingPlugin.logger.isLoggable(Level.FINE))
			VDataBindingPlugin.logger.log(Level.FINE, "Path: " + propertyPath + ": Target object is null (" + "IObservableValue " + object + " returned null)");
	} else {
		element = object;
		if (element == null && VDataBindingPlugin.logger.isLoggable(Level.FINE))
			VDataBindingPlugin.logger.log(Level.FINE, "Path: " + propertyPath + ": Target object is null");
	}
	if (element == null) {
		return null;
	}
	
	for (int i=0; i<pathTokens.size()-1;i++) {
		String pathToken = pathTokens.get(i);
		PropertyDescriptor propDescr = getPropertyDescriptor(element, pathToken);
		if (propDescr.getReadMethod() == null) {
			if (VDataBindingPlugin.logger.isLoggable(Level.FINE))
				VDataBindingPlugin.logger.log(Level.FINE, "Path: " + propertyPath + ": Target object is null, because property " + propDescr.getName() + " has no read method");

			element = null;
			break;
		}
		try {
			element = propDescr.getReadMethod().invoke(element, (Object[])null);
			if (element == null) {
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		int listIndex = getListIndex(pathToken);
		if (listIndex != -1) {
			element = ((List)element).get(listIndex);
		}
	}
	if (element == null && VDataBindingPlugin.logger.isLoggable(Level.FINE))
		VDataBindingPlugin.logger.log(Level.FINE, "Path: " + propertyPath + ": Target object is null");
	return element;
}

int getListIndex(String token) {
	int si = token.indexOf("[");
	if (si == -1)
		return -1;
	int ei = token.indexOf("]");
	String indexStr = token.substring(si+1, ei);
	return Integer.parseInt(indexStr);
}

private PropertyDescriptor getPropertyDescriptor(Object target, String propertyName) {
	propertyName = removeIndexBrackets(propertyName);
	Class clazz = target.getClass();
	if(clazz.getSuperclass().isEnum()) {
		return getPropertyDescriptor(clazz.getSuperclass(), propertyName);
	} else {
		return getPropertyDescriptor(clazz, propertyName);
	}
}

private PropertyDescriptor getPropertyDescriptor(Class clazz, String propertyName) {
	if (clazz == null) {
		VDataBindingPlugin.logger.log(Level.SEVERE, "Class is null");
		throw new IllegalArgumentException();
	}
	PropertyDescriptor prop = cachedPropertyDescriptorMap.get(clazz);
	if (prop != null) return prop;
	try {
		Class curClazz = clazz;
		BeanInfo beanInfo = Introspector.getBeanInfo(curClazz);
		prop = fetchPropertyDescriptor(beanInfo, propertyName);
		cachedPropertyDescriptorMap.put(clazz, prop);

		if (prop.getReadMethod() == null)
			VDataBindingPlugin.logger.log(Level.WARNING, "Path: " + propertyPath + ": Property " + prop.getName() + " has no read method.");
		if (prop.getWriteMethod() == null)
			VDataBindingPlugin.logger.log(Level.FINE, "Path: " + propertyPath + ": Property " + prop.getName() + " has no write method.");

		
		return prop;
	} catch (IntrospectionException e) {
		throw new RuntimeException();
	}
}

String removeIndexBrackets(String token) {
	int si = token.indexOf("[");
	if (si == -1)
		return token;
	return token.substring(0, si);
}

public static Class fetchDeclaredTypeForPath(Class clazz, String propertyPath) {
	StringTokenizer t = new StringTokenizer(propertyPath, ".");
	while (t.hasMoreTokens()) {
		String prop = t.nextToken();
		PropertyDescriptor propDescr;
		try {
			propDescr = fetchPropertyDescriptor(Introspector.getBeanInfo(clazz), prop);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		clazz = propDescr.getPropertyType();
	}
	return clazz;
}

public static PropertyDescriptor fetchPropertyDescriptor(BeanInfo beanInfo, String propertyName) {
	for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors())
		if (prop.getName().equals(propertyName))
			return prop;
	try {
		if (VDataBindingPlugin.logger.isLoggable(Level.FINE))
			VDataBindingPlugin.logger.log(Level.FINE, "Property named " + propertyName + " does not exist for bean " + beanInfo.getBeanDescriptor().getName());
		return new UndefinedPropertyDescriptor(propertyName);
	} catch (IntrospectionException e) {
		throw new RuntimeException(e);
	}
}

//TODO make it customizable if property types are determined in a static or a dynamic way
Class getPropertyType() {
	if (object instanceof VStructuredViewerSelectionObservableValue) {
		return getStaticPropertyType(((VStructuredViewerSelectionObservableValue)object).targetType);
	}
	Object targetObject = readTargetObject();
	if (targetObject == null) return Object.class;
	String lastPathComponent = pathTokens.get(pathTokens.size()-1);
	if (lastPathComponent.equals("this"))
		return targetObject.getClass();
	PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, lastPathComponent);
	return propDescr.getPropertyType();
}

String getLastPathComponent() {
	return pathTokens.get(pathTokens.size()-1);
}

private Class getStaticPropertyType(Class targetType) {
	Class type = targetType;
	Iterator<String> pathTokenIterator = pathTokens.iterator();
	while (pathTokenIterator.hasNext()) {
		String propertyName = pathTokenIterator.next();
		PropertyDescriptor propertyDescr = getPropertyDescriptor(type, propertyName);
		type = propertyDescr.getPropertyType();
	}
	return type;
}

public static boolean isSimpleProperty(String propertyPath) {
	if (propertyPath.indexOf(".") != -1) return false;
	if (propertyPath.indexOf("[") != -1) return false;
	return true;
}


}
