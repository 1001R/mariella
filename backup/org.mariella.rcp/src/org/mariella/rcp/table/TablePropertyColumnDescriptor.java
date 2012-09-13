package org.mariella.rcp.table;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.mariella.rcp.beans.PropertyAccessor;
import org.mariella.rcp.table.edit.TableColumnEditHandler;


public class TablePropertyColumnDescriptor extends TableColumnDescriptor implements PropertyAccessor {

class UndefinedPropertyDescriptor extends PropertyDescriptor {
public UndefinedPropertyDescriptor(String propertyName) throws IntrospectionException {
	super(propertyName, null, null);
}
}

String propertyPath;
List<String> pathTokens;
Format format;
TableColumnEditHandler editHandler;
Map<Class, PropertyDescriptor> propertyDescriptorMap = new HashMap<Class, PropertyDescriptor>();

//PropertyDescriptor propertyDescriptor;

public TablePropertyColumnDescriptor(String header, String propertyPath) {
	super(header);
	this.propertyPath = propertyPath;
	initialize();
}

public TablePropertyColumnDescriptor(String header, String propertyPath, Format format) {
	super(header);
	this.propertyPath = propertyPath;
	this.format = format;
	initialize();
}

public TablePropertyColumnDescriptor(String header, String propertyPath, int weight) {
	super(header, weight);
	this.propertyPath = propertyPath;
	initialize();
}

public TablePropertyColumnDescriptor(String header, String propertyPath, Format format, int weight) {
	super(header, weight);
	this.propertyPath = propertyPath;
	this.format = format;
	initialize();
}

private void initialize() {
	StringTokenizer t = new StringTokenizer(propertyPath, ".");
	pathTokens = new ArrayList<String>(t.countTokens());
	while (t.hasMoreTokens())
		pathTokens.add(t.nextToken());
}

public String getPropertyPath() {
	return propertyPath;
}

private PropertyDescriptor getPropertyDescriptor(Object target, String propertyName) {
	Class clazz = target.getClass();
	PropertyDescriptor prop = propertyDescriptorMap.get(clazz);
	if (prop != null) return prop;
	try {
		StringTokenizer t = new StringTokenizer(propertyName);
		Class curClazz = clazz;
		while (t.hasMoreTokens()) {
			String propName = t.nextToken();
			BeanInfo beanInfo = Introspector.getBeanInfo(curClazz);
			prop = fetchPropertyDescriptor(beanInfo, propName);
			
		}
		propertyDescriptorMap.put(clazz, prop);
		return prop;
	} catch (IntrospectionException e) {
		throw new RuntimeException();
	}
}

public Object readValue(Object element) {
	try {
		Object targetObject = readTargetObject(element);
		if (targetObject == null) return null;
		PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, pathTokens.get(pathTokens.size()-1));
		if (propDescr.getReadMethod() == null) return null;
		return propDescr.getReadMethod().invoke(targetObject, (Object[])null);
	} catch (Exception e) {
		throw new RuntimeException();
	}
}

private Object readTargetObject(Object root) {
	Object element = root;
	for (int i=0; i<pathTokens.size()-1;i++) {
		PropertyDescriptor propDescr = getPropertyDescriptor(element, pathTokens.get(i));
		if (propDescr.getReadMethod() == null)
			return null;
		try {
			element = propDescr.getReadMethod().invoke(element, (Object[])null);
			if (element == null) return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	return element;
}

public boolean canRead(Object element) {
	Object targetObject = readTargetObject(element);
	if (targetObject == null) return false;
	PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, pathTokens.get(pathTokens.size()-1));
	return propDescr.getReadMethod() != null;
}

public boolean canWrite(Object element) {
	Object targetObject = readTargetObject(element);
	if (targetObject == null) return false;
	PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, pathTokens.get(pathTokens.size()-1));
	return propDescr.getWriteMethod() != null;
}

public void writeValue(Object element, Object value) {
	try {
		Object targetObject = readTargetObject(element);
		if (targetObject == null) return;
		PropertyDescriptor propDescr = getPropertyDescriptor(targetObject, pathTokens.get(pathTokens.size()-1));
		if (propDescr.getWriteMethod() == null) return;
		propDescr.getWriteMethod().invoke(targetObject, value);
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

private PropertyDescriptor fetchPropertyDescriptor(BeanInfo beanInfo, String propertyName) {
	for (PropertyDescriptor prop : beanInfo.getPropertyDescriptors())
		if (prop.getName().equals(propertyName))
			return prop;
	try {
		return new UndefinedPropertyDescriptor(propertyName);
	} catch (IntrospectionException e) {
		throw new RuntimeException(e);
	}
}

public Format getFormat() {
	return format;
}

public TableColumnEditHandler getEditHandler() {
	return editHandler;
}

public void setEditHandler(TableColumnEditHandler editHandler) {
	this.editHandler = editHandler;
	this.editHandler.setTableColumnDescriptor(this);
}

public void setFormat(Format format) {
	this.format = format;
}

}
