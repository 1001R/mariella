package org.mariella.persistence.loader;

import org.mariella.persistence.schema.ClassDescription;


public interface ModifiableFactory {

public Object createModifiable(ClassDescription classDescription);
public Object createEmbeddable(ClassDescription classDescription);

}
