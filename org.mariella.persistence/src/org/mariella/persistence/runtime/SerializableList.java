package org.mariella.persistence.runtime;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class SerializableList<T> extends AbstractList<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private transient List<T> list = new ArrayList<T>();

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int size() {
		return list.size();
	}
	
	public void add(int index, T element) {
		list.add(index, element);
	}

	public T set(int index, T element) {
		return list.set(index, element);
	}
	
	@Override
	public T remove(int index) {
		return list.remove(index);
	}
	
    private void writeObject(ObjectOutputStream s) throws java.io.IOException {
    	for (T element : this) {
    		if (element != null && element instanceof Serializable) {
    			s.writeObject(element);
    		}
    	}
    	s.writeObject(null);
    }

    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        list = new ArrayList<T>();
        Object element;
        while ((element = s.readObject()) != null) {
        	list.add((T) element);
        }
    }
	
}
