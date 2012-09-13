package org.mariella.persistence.query;

import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.PropertyDescription;

public interface PathExpressionVisitor {

public ClassDescription root(String token);
public void property(ClassDescription classDescription, PropertyDescription propertyDescription);
public void property(ClassDescription classDescription, ClassDescription castTo, PropertyDescription propertyDescription);
public void unknownOrAmbigousProperty(ClassDescription classDescription, String propertyName);
public void unknownType(String className);
public void invalidCast(ClassDescription classDescription, ClassDescription castTo);
public void afterEnd(String token);
}
