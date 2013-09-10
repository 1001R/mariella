package org.mariella.oxygen.util;

import java.util.Iterator;

public abstract class FilteredIterator<T>  implements Iterator<T> {
    private final static long serialVersionUID = 1L;
    private Iterator<T> baseIterator = null;
    private T nextElement = null;
    private int maxItems = -1;
    private int currentItem = -1;

public FilteredIterator(Iterator<T> base) {
    super();
    baseIterator = base;
}

public FilteredIterator(Iterator<T> base, int maxItems) {
    super();
    baseIterator = base;
    this.maxItems = maxItems;
}

private boolean findNextElement() {
    while (baseIterator.hasNext()) {
        nextElement = getElementFor(baseIterator.next());
        if (match(nextElement)) {
            return true;
        }
    }
    nextElement = null;
    return false;
}

public T getElementFor(T baseElement) {
    return baseElement;
}

public boolean hasNext() {
    if (maxItems >= 0 && currentItem >= maxItems-1)
        return false;
    return findNextElement();
}

public abstract boolean match(Object element);

public T next() {
    currentItem++;
    return nextElement;
}

public void remove()  {
}

}
