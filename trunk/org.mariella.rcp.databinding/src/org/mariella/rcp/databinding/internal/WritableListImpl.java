package org.mariella.rcp.databinding.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.mariella.rcp.databinding.VBindingContext;

/**
 * Same as WritableList, except no Realm check on read ("getterCalled") 
 * 
 * @author maschmid
 *
 */
public class WritableListImpl extends WritableList {

public WritableListImpl(VBindingContext ctx) {
}

public WritableListImpl(VBindingContext ctx, List toWrap) {
	super(toWrap, null);
}

public boolean contains(Object o) {
	return wrappedList.contains(o);
}

public boolean containsAll(Collection c) {
	return wrappedList.containsAll(c);
}

public boolean equals(Object o) {
	return wrappedList.equals(o);
}

public Object get(int index) {
	return wrappedList.get(index);
}

public int hashCode() {
	return wrappedList.hashCode();
}

public int indexOf(Object o) {
	return wrappedList.indexOf(o);
}

public boolean isEmpty() {
	return wrappedList.isEmpty();
}

public Iterator iterator() {
	final Iterator wrappedIterator = wrappedList.iterator();
	return new Iterator() {

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return wrappedIterator.hasNext();
		}

		public Object next() {
			return wrappedIterator.next();
		}
	};
}

public int lastIndexOf(Object o) {
	return wrappedList.lastIndexOf(o);
}

public ListIterator listIterator(int index) {
	final ListIterator wrappedIterator = wrappedList.listIterator(index);
	return new ListIterator() {

		public int nextIndex() {
			return wrappedIterator.nextIndex();
		}

		public int previousIndex() {
			return wrappedIterator.previousIndex();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return wrappedIterator.hasNext();
		}

		public boolean hasPrevious() {
			return wrappedIterator.hasPrevious();
		}

		public Object next() {
			return wrappedIterator.next();
		}

		public Object previous() {
			return wrappedIterator.previous();
		}

		public void add(Object o) {
			throw new UnsupportedOperationException();
		}

		public void set(Object o) {
			throw new UnsupportedOperationException();
		}
	};
}

public int size() {
	return wrappedList.size();
}

public List subList(int fromIndex, int toIndex) {
	return wrappedList.subList(fromIndex, toIndex);
}

public Object[] toArray() {
	return wrappedList.toArray();
}

public Object[] toArray(Object[] a) {
	return wrappedList.toArray(a);
}

public String toString() {
	return wrappedList.toString();
}
}
