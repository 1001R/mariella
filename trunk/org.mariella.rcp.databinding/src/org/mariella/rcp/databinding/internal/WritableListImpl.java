package org.mariella.rcp.databinding.internal;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.mariella.rcp.databinding.VBindingContext;

/**
 * Same as WritableList, except no Realm check on read ("getterCalled") 
 * 
 * @author maschmid
 *
 */
public class WritableListImpl extends WritableList {

private class Itr implements Iterator {
/**
 * Index of element to be returned by subsequent call to next.
 */
int cursor = 0;

/**
 * Index of element returned by most recent call to next or previous. Reset to
 * -1 if this element is deleted by a call to remove.
 */
int lastRet = -1;

public boolean hasNext() {
	return cursor != size();
}

public Object next() {
	try {
		Object next = get(cursor);
		lastRet = cursor++;
		return next;
	} catch (IndexOutOfBoundsException e) {
		throw new NoSuchElementException();
	}
}

public void remove() {
	if (lastRet == -1)
		throw new IllegalStateException();

	try {
		WritableListImpl.this.remove(lastRet);
		if (lastRet < cursor)
			cursor--;
		lastRet = -1;
	} catch (IndexOutOfBoundsException e) {
		throw new ConcurrentModificationException();
	}
}
}



private class ListItr extends Itr implements ListIterator {
ListItr(int index) {
	cursor = index;
}

public boolean hasPrevious() {
	return cursor != 0;
}

public Object previous() {
	try {
		int i = cursor - 1;
		Object previous = get(i);
		lastRet = cursor = i;
		return previous;
	} catch (IndexOutOfBoundsException e) {
		throw new NoSuchElementException();
	}
}

public int nextIndex() {
	return cursor;
}

public int previousIndex() {
	return cursor - 1;
}

public void set(Object e) {
	if (lastRet == -1)
		throw new IllegalStateException();

	try {
		WritableListImpl.this.set(lastRet, e);
	} catch (IndexOutOfBoundsException ex) {
		throw new ConcurrentModificationException();
	}
}

public void add(Object e) {

	try {
		WritableListImpl.this.add(cursor++, e);
		lastRet = -1;
	} catch (IndexOutOfBoundsException ex) {
		throw new ConcurrentModificationException();
	}
}
}



public WritableListImpl(VBindingContext ctx) {
}

public WritableListImpl(VBindingContext ctx, List toWrap) {
	super(toWrap, null);
}

@Override
public boolean contains(Object o) {
	return wrappedList.contains(o);
}

@Override
public boolean containsAll(Collection c) {
	return wrappedList.containsAll(c);
}

@Override
public boolean equals(Object o) {
	return wrappedList.equals(o);
}

@Override
public Object get(int index) {
	return wrappedList.get(index);
}

@Override
public int hashCode() {
	return wrappedList.hashCode();
}

@Override
public int indexOf(Object o) {
	return wrappedList.indexOf(o);
}

@Override
public boolean isEmpty() {
	return wrappedList.isEmpty();
}

@Override
public Iterator iterator() {
	return new Itr();
}

@Override
public int lastIndexOf(Object o) {
	return wrappedList.lastIndexOf(o);
}

@Override
public ListIterator listIterator(int index) {
	return new ListItr(index);
}

@Override
public int size() {
	return wrappedList.size();
}

@Override
public List subList(int fromIndex, int toIndex) {
	return wrappedList.subList(fromIndex, toIndex);
}

@Override
public Object[] toArray() {
	return wrappedList.toArray();
}

@Override
public Object[] toArray(Object[] a) {
	return wrappedList.toArray(a);
}

@Override
public String toString() {
	return wrappedList.toString();
}
}
