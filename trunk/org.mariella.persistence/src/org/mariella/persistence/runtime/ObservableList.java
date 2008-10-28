package org.mariella.persistence.runtime;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("unchecked")
public abstract class ObservableList<T> implements List<T> {
	private final String propertyName;
	private List<T> list = new ArrayList<T>();
	
public ObservableList(String propertyName) {
	super();
	this.propertyName = propertyName;
}
	
protected abstract PropertyChangeSupport getPropertyChangeSupport();

public boolean add(T e) {
	if(!contains(e)) {
		if(list.add(e)) {
			getPropertyChangeSupport().fireIndexedPropertyChange(propertyName, size() - 1, null, e);
			return true;
		}
	}
	return false;
}



public void add(int index, T element) {
	throw new UnsupportedOperationException();
}

public boolean addAll(Collection c) {
	throw new UnsupportedOperationException();
}

public boolean addAll(int index, Collection c) {
	throw new UnsupportedOperationException();
}

public void clear() {
	throw new UnsupportedOperationException();
}

public boolean contains(Object o) {
	return list.contains(o);
}

public boolean containsAll(Collection c) {
	throw new UnsupportedOperationException();
}

public T get(int index) {
	return list.get(index);
}

public int indexOf(Object o) {
	return list.indexOf(o);
}

public boolean isEmpty() {
	return list.isEmpty();
}

public Iterator<T> iterator() {
	return list.iterator();
}

public int lastIndexOf(Object o) {
	return list.lastIndexOf(o);
}

public ListIterator<T> listIterator() {
	return list.listIterator();
}

public ListIterator<T> listIterator(int index) {
	return list.listIterator(index);
}

public boolean remove(Object o) {
	int idx = indexOf(o);
	if(idx > -1) {
		list.remove(o);
		getPropertyChangeSupport().fireIndexedPropertyChange(propertyName, idx, o, null);
		return true;
	}
	return false;
}

public T remove(int index) {
	T object = list.remove(index);
	getPropertyChangeSupport().fireIndexedPropertyChange(propertyName, index, object, null);
	return object;
}

public boolean removeAll(Collection c) {
	throw new UnsupportedOperationException();
}

public boolean retainAll(Collection c) {
	throw new UnsupportedOperationException();
}

public T set(int index, T element) {
	T oldValue = list.set(index, element);
	getPropertyChangeSupport().fireIndexedPropertyChange(propertyName, index, oldValue, element);
	return oldValue;
}

public int size() {
	return list.size();
}

public List<T> subList(int fromIndex, int toIndex) {
	return list.subList(fromIndex, toIndex);
}

public Object[] toArray() {
	return list.toArray();
}

public Object[] toArray(Object[] a) {
	return list.toArray(a);
}

@Override
public String toString() {
	return list.toString();
}

}
