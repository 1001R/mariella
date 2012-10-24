package org.mariella.oxygen.util;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class AbstractIdentityMap implements Map {
    private transient Set keySet = null;
    private transient Collection values = null;
    
protected AbstractIdentityMap() {}

public void clear() {
    entrySet().clear();
}

public boolean containsKey(Object key) {
    Iterator i = entrySet().iterator();
    if (key == null) {
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (e.getKey() == null)
                return true;
        }
    } else {
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (key == e.getKey())
                return true;
        }
    }
    return false;
}

public boolean containsValue(Object value) {
    Iterator i = entrySet().iterator();
    if (value == null) {
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (e.getValue() == null)
                return true;
        }
    } else {
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (value.equals(e.getValue()))
                return true;
        }
    }
    return false;
}

public abstract Set entrySet();

public boolean equals(Object o) {
    if (o == this)
        return true;
    if (!(o instanceof Map))
        return false;
    Map t = (Map) o;
    if (t.size() != size())
        return false;
    Iterator i = entrySet().iterator();
    while (i.hasNext()) {
        Entry e = (Entry) i.next();
        Object key = e.getKey();
        Object value = e.getValue();
        if (value == null) {
            if (!(t.get(key) == null && t.containsKey(key)))
                return false;
        } else {
            if (!value.equals(t.get(key)))
                return false;
        }
    }
    return true;
}

public Object get(Object key) {
    Iterator i = entrySet().iterator();
    if (key == null) {
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (e.getKey() == null)
                return e.getValue();
        }
    } else {
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            if (key == e.getKey())
                return e.getValue();
        }
    }
    return null;
}

public int hashCode() {
    int h = 0;
    Iterator i = entrySet().iterator();
    while (i.hasNext())
        h += i.next().hashCode();
    return h;
}

public boolean isEmpty() {
    return size() == 0;
}

public Set keySet() {
    if (keySet == null) {
        keySet = new AbstractSet() {
                     public Iterator iterator() {
                         return new Iterator() {
                                    private Iterator i = entrySet().iterator();
                                    public boolean hasNext() {
                                        return i.hasNext();
                                    }
                                    public Object next() {
                                        return ((Entry) i.next()).getKey();
                                    }
                                    public void remove
                                        () {
                                        i.remove();
                                    }
                                };
                     }
                     public int size() {
                         return AbstractIdentityMap.this.size();
                     }
                     public boolean contains(Object k) {
                         return AbstractIdentityMap.this.containsKey(k);
                     }
                 };
    }
    return keySet;
}

public Object put(Object key, Object value) {
    throw new UnsupportedOperationException();
}

public void putAll(Map t) {
    Iterator i = t.entrySet().iterator();
    while (i.hasNext()) {
        Entry e = (Entry) i.next();
        put(e.getKey(), e.getValue());
    }
}

public Object remove(Object key) {
    Iterator i = entrySet().iterator();
    Entry correctEntry = null;
    if (key == null) {
        while (correctEntry == null && i.hasNext()) {
            Entry e = (Entry) i.next();
            if (e.getKey() == null)
                correctEntry = e;
        }
    } else {
        while (correctEntry == null && i.hasNext()) {
            Entry e = (Entry) i.next();
            if (key == e.getKey())
                correctEntry = e;
        }
    }
    Object oldValue = null;
    if (correctEntry != null) {
        oldValue = correctEntry.getValue();
        i.remove();
    }
    return oldValue;
}

public int size() {
    return entrySet().size();
}

public String toString() {
    int max = size() - 1;
    StringBuffer buf = new StringBuffer();
    Iterator i = entrySet().iterator();
    buf.append("{");
    for (int j = 0; j <= max; j++) {
        Entry e = (Entry) (i.next());
        buf.append(e.getKey() + "=" + e.getValue());
        if (j < max)
            buf.append(", ");
    }
    buf.append("}");
    return buf.toString();
}

public Collection values() {
    if (values == null) {
        values = new AbstractCollection() {
                     public Iterator iterator() {
                         return new Iterator() {
                                    private Iterator i = entrySet().iterator();
                                    public boolean hasNext() {
                                        return i.hasNext();
                                    }
                                    public Object next() {
                                        return ((Entry) i.next()).getValue();
                                    }
                                    public void remove
                                        () {
                                        i.remove();
                                    }
                                };
                     }
                     public int size() {
                         return AbstractIdentityMap.this.size();
                     }
                     public boolean contains(Object v) {
                         return AbstractIdentityMap.this.containsValue(v);
                     }
                 };
    }
    return values;
}

}
