package org.mariella.rcp.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObservableList<E> extends AbstractList<E> {

private List items;
private List<ListObserver> observers = new ArrayList<ListObserver>();

public ObservableList(List items) {
	this.items = items;
}

public E get(int index) {
	return (E)items.get(index);
}

public int size() {
	return items.size();
}

public boolean addAll(Collection<? extends E> c) {
	throw new UnsupportedOperationException();
}

public void add(int index, E element) {
	items.add(index, element);
	fireElementAdded(index, element);
	fireModified();
}

public boolean contains(Object o) {
	return items.contains(o);
}

public boolean remove(Object o) {
	boolean rv = items.remove(o);
	fireElementRemoved((E)o);
	fireModified();
	return rv;
}

public E remove(int index) {
	E element = (E) items.remove(index);
	fireElementRemoved(element);
	fireModified();
	return element;
}

public boolean removeAll(Collection c) {
	throw new UnsupportedOperationException();
}

private void fireModified() {
	for (ListObserver o : observers)
		o.listModified();
}

private void fireElementAdded(int index, E element) {
	for (ListObserver<E> o : observers)
		o.elementAdded(index, element);
}

private void fireElementRemoved(E element) {
	for (ListObserver<E> o : observers)
		o.elementRemoved(element);
}

public void addObserver(ListObserver o) {
	observers.add(o);
}

}
