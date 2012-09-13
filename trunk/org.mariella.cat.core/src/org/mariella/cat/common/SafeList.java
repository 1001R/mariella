package org.mariella.cat.common;

import java.util.Iterator;

/**
 * A list that can be modified while iterating. An iterator always steps the elements existent when the iteration starts. 
 * Modification are not reflected into a running iteration.<br>
 * <br>
 * Example: Listeners-List
 */
public class SafeList<T> implements Iterable<T> {
	
	private Object[] elements = null;
	
	public SafeList() {
	}
	
	public void add(T element) {
		if (elements == null) {
			elements = new Object[1];
		} else {
			Object[] newElements = new Object[elements.length+1];
			System.arraycopy(elements, 0, newElements, 0, elements.length);
			elements = newElements;
		}
		elements[elements.length-1] = element;
	}
	
	public boolean remove(T element) {
		boolean removed = false;
		if (elements != null && element != null) {
			Object[] newElements = new Object[elements.length-1];
			int j = 0;
			for (int i = 0; i < elements.length; i++) {
				if (!removed && elements[i].equals(element)) {
					removed = true;
				} else if (j < newElements.length) {
					newElements[j++] = elements[i];
				}
			}
			if (removed) {
				if (newElements.length == 0) {
					elements = null;
				} else {
					elements = newElements;
				}
			}
		}
		return removed;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Object[] itElements = elements;
			private int cursor = 0;
			@Override
			public boolean hasNext() {
				return itElements != null && cursor < itElements.length;
			}
			@Override
			public T next() {
				return (T) itElements[cursor++];
			}
			@Override
			public void remove() {
				SafeList.this.remove((T) itElements[cursor-1]);
			}
		};
	}

}
