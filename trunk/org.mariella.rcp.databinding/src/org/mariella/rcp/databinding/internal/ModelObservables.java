package org.mariella.rcp.databinding.internal;

import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.mariella.rcp.databinding.VBindingContext;

public class ModelObservables {

public static IObservableValue observeValue(Object object, String propertyPath, Class valueType) {
	return new PropertyPathObservableValue(object, propertyPath, valueType);
}

public static IObservableList createObservableList(VBindingContext ctx) {
	return new WritableListImpl(ctx);
}

public static IObservableList createObservableList(VBindingContext ctx, List wrapped) {
	return new WritableListImpl(ctx, wrapped);
}

public static IObservableList observeList(VBindingContext ctx, Object object, String propertyPath, Class type) {
	/*
	PropertyPathSupport supp = new PropertyPathSupport();
	supp.object = object;
	supp.propertyPath = propertyPath;
	supp.initialize();
	Object value = supp.implementDoGetValue();
	if (value instanceof IObservableList)
		return (IObservableList)value;
	*/
	return new PropertyPathObservableList(ctx, object, propertyPath, type);
}

}
