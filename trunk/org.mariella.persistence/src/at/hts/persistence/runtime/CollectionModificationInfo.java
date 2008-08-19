package at.hts.persistence.runtime;

import java.beans.IndexedPropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionModificationInfo {
	private Collection<Object> added = null;
	private Collection<Object> removed = null;
	
public Collection<Object> getAdded() {
	if(added == null) {
		added = new ArrayList<Object>();
	}
	return added;
}

public Collection<Object> getRemoved() {
	if(removed == null) {
		removed = new ArrayList<Object>();
	}
	return removed;
}

public void changed(IndexedPropertyChangeEvent event) {
	if(event.getOldValue() == null && event.getNewValue() != null) {
		added(event.getNewValue());
	} else if(event.getOldValue() != null) {
		removed(event.getOldValue());
		if(event.getNewValue() != null) {
			added(event.getNewValue());
		}
	}
}

private void added(Object object) {
	if(getRemoved().contains(object)) {
		getRemoved().remove(object);
	} else {
		getAdded().add(object);
	}
}

private void removed(Object object) {
	if(getAdded().contains(object)) {
		getAdded().remove(object);
	} else {
		getRemoved().add(object);
	}
}

}
