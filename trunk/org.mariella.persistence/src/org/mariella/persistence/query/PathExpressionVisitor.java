package org.mariella.persistence.query;

import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;

public interface PathExpressionVisitor {

public ClassDescription root(String token);
public void property(ClassDescription classDescription, PropertyDescription propertyDescription);
public void unknownProperty(ClassDescription classDescription, String propertyName);
public void afterEnd(String token);
}
