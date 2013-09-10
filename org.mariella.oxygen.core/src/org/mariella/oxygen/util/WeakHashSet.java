package org.mariella.oxygen.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

@SuppressWarnings("unchecked")
public class WeakHashSet extends AbstractSet implements Set, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private transient WeakHashMap map;
    private static final Object PRESENT = new Object();
    
public WeakHashSet() {
    map = new WeakHashMap();
}

public WeakHashSet(int initialCapacity) {
    map = new WeakHashMap(initialCapacity);
}

public WeakHashSet(int initialCapacity, float loadFactor) {
    map = new WeakHashMap(initialCapacity, loadFactor);
}

public WeakHashSet(Collection c) {
    map = new WeakHashMap(Math.max(2*c.size(), 11));
    addAll(c);
}

public boolean add(Object o) {
    return map.put(o, PRESENT)==null;
}

public void clear() {
    map.clear();
}

public boolean contains(Object o) {
    return map.containsKey(o);
}

public boolean isEmpty() {
    return map.isEmpty();
}

public Iterator iterator() {
    return map.keySet().iterator();
}

public boolean remove
    (Object o) {
    return map.remove(o)==PRESENT;
}

public int size() {
    return map.size();
}

}
