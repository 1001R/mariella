package org.mariella.persistence.mapping;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public class ReflectionUtil {

public static Type readCollectionElementType(Type t) {
	if ( t != null && t instanceof ParameterizedType ) {
		return readActualTypeArgument((ParameterizedType)t);
	}
	return null;
}

public static Type readTypeArgumentsOfClass(Class clazz) {
	Type t = clazz.getGenericSuperclass();
	if ( t instanceof ParameterizedType ) {
		return readActualTypeArgument((ParameterizedType)t);
	}
	return null;
}

public static Type readActualTypeArgument(ParameterizedType pt) {
	Type[] genTypes = pt.getActualTypeArguments();
	if ( genTypes.length == 1 && genTypes[0] instanceof Class ) {
		return (Class) genTypes[0];
	}
	else if ( genTypes.length == 2 && genTypes[1] instanceof Class ) {
		return (Class) genTypes[1];
	}
	return null;
}


public static Type readType(AnnotatedElement annotatedElement) {
	if (annotatedElement instanceof Field)
		return ((Field)annotatedElement).getGenericType();
	return ((Method)annotatedElement).getGenericReturnType();
}

public static String readFieldOrPropertyName(AnnotatedElement annotatedElement) {
	try {
		if (annotatedElement instanceof Field)
			return ((Field)annotatedElement).getName();
		
		// assume it is getter method from  property
		// TODO find faster way
		PropertyDescriptor[] props = Introspector.getBeanInfo(((Method)annotatedElement).getDeclaringClass()).getPropertyDescriptors();
		for (PropertyDescriptor prop : props) {
			if (annotatedElement.equals(prop.getReadMethod()))
				return prop.getName();
		}
		throw new RuntimeException("Could not find corresponding property of method " + annotatedElement);
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

public static boolean hasAnyEJB3Annotations(AnnotatedElement annotatedElement) {
	for (Annotation anno : annotatedElement.getAnnotations()) {
		if (anno.getClass().getPackage().getName().equals("javax.persistence")) return true;
	}
	return false;
}

public static PropertyDescriptor getPropertyDescriptor(Class clazz, String propName) {
	try {
		for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors())
			if (prop.getName().equals(propName))
				return prop;
		return null;
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

public static Field getField(Class clazz, String name) {
	for (Field declared : clazz.getDeclaredFields())
		if (declared.getName().equals(name)) return declared;
	return null;
}

public static String buildPropertyName(String fieldName) {
	if (fieldName.length() == 1) return fieldName;
	if (Character.isUpperCase(fieldName.charAt(1)))
		return String.valueOf(Character.toUpperCase(fieldName.charAt(0))) + fieldName.substring(1);
	return fieldName;
}

}
