package org.mariella.rcp.databinding.internal;

import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.mariella.rcp.databinding.VDataBindingContext;

public class ModelObservables {

public static IObservableValue observeValue(Object object, String propertyPath, Class valueType) {
	return new PropertyPathObservableValue(Realm.getDefault(), object, propertyPath, valueType);
}

public static IObservableList createObservableList(VDataBindingContext ctx) {
	return new WritableListImpl(ctx);
}

public static IObservableList createObservableList(VDataBindingContext ctx, List wrapped) {
	return new WritableListImpl(ctx, wrapped);
}

public static IObservableList observeList(VDataBindingContext ctx, Object object, String propertyPath, Class type) {
	/*
	PropertyPathSupport supp = new PropertyPathSupport();
	supp.object = object;
	supp.propertyPath = propertyPath;
	supp.initialize();
	Object value = supp.implementDoGetValue();
	if (value instanceof IObservableList)
		return (IObservableList)value;
	*/
	return new PropertyPathObservableList(ctx, Realm.getDefault(), object, propertyPath, type);
}

}
