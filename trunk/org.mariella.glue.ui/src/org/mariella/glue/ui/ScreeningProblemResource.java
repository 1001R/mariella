package org.mariella.glue.ui;

import org.mariella.rcp.problems.AbstractEditorProblemResource;
import org.mariella.rcp.resources.VResourceRef;

public class ScreeningProblemResource extends AbstractEditorProblemResource {
	private VResourceRef ref;
	private String description;

public ScreeningProblemResource(UIRegistration<?> registration, VResourceRef ref, String description) {
	super();
	this.ref = ref;
	this.description = description;
	setProvider(registration.getResourceManager());
	setEditorId(registration.getEditorId());
	setElementFactoryId(registration.getResourceManager().getElementFactoryId());
	ref.storeToMemento(getEditorMemento());
}

public VResourceRef getRef() {
	return ref;
}

@Override
public boolean equals(Object obj) {
	if(obj == null) {
		return false;
	} else if(obj.getClass() != getClass()) {
		return false;
	} else {
		return ((ScreeningProblemResource)obj).ref.equals(ref);
	}
}

@Override
public String getDescription() {
	return description;
}

@Override
public int hashCode() {
	return ref.hashCode();
}

}
