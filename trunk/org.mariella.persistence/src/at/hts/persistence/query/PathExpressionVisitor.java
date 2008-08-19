package at.hts.persistence.query;

import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;

public interface PathExpressionVisitor {

public ClassDescription root(String token);
public void property(ClassDescription classDescription, PropertyDescription propertyDescription);
public void unknownProperty(ClassDescription classDescription, String propertyName);
public void afterEnd(String token);
}
