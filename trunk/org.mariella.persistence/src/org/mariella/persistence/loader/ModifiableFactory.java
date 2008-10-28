package org.mariella.persistence.loader;

import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.schema.ClassDescription;


public interface ModifiableFactory {

public Modifiable createModifiable(ClassDescription classDescription);
public Object createEmbeddable(ClassDescription classDescription);

}
