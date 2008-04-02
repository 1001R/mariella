package org.mariella.rcp.problems;

import org.mariella.rcp.resources.VResourceRef;

public class VResourceProblemResource extends ProblemResource {
private VResourceRef ref;
private String description;


public VResourceProblemResource(ProblemsProvider provider, VResourceRef ref, String editorId, String elementFactoryId, String description) {
	super();
	this.ref = ref;
	this.description = description;
	setProvider(provider);
	setEditorId(editorId);
	setElementFactoryId(elementFactoryId);
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
		return ((VResourceProblemResource)obj).ref.equals(ref);
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
