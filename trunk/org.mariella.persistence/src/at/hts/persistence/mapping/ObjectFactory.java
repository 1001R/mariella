package at.hts.persistence.mapping;

import at.hts.persistence.schema.PropertyDescription;

public interface ObjectFactory {

public Object getObject(ClassMapping classMapping, Object identity);
public Object createObject(ClassMapping classMapping, Object identity);
public Object createEmbeddableObject(AbstractClassMapping classMapping);
public Object getValue(Object receiver, PropertyDescription propertyDescription);
public void setValue(Object receiver, PropertyDescription propertyDescription, Object value);
public void updateValue(Object receiver, PropertyDescription propertyDescription, Object value);

}
