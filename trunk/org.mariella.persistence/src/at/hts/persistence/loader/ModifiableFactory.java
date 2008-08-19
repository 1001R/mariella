package at.hts.persistence.loader;

import at.hts.persistence.runtime.Modifiable;
import at.hts.persistence.schema.ClassDescription;

public interface ModifiableFactory {

public Modifiable createModifiable(ClassDescription classDescription);
public Object createEmbeddable(ClassDescription classDescription);

}
