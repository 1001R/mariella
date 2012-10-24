package org.mariella.oxygen.util;

import java.util.Iterator;

public class EmptyIterator<T> implements Iterator<T> {

public EmptyIterator() {
    super();
}

public boolean hasNext() {
    return false;
}

public T next() {
    return null;
}

public void remove() {
}

}
