package org.mariella.persistence.query;

import java.util.List;

import org.mariella.persistence.schema.ClassDescription;
import org.mariella.persistence.schema.CollectionPropertyDescription;
import org.mariella.persistence.schema.RelationshipPropertyDescription;

public interface ClusterVisitor {

public void beginPathExpression(String pathExpression);
public void endPathExpression(String pathExpression);

public boolean root(Object entity);
public boolean property(Object owner, ClassDescription ownerClassDescription, RelationshipPropertyDescription propertyDescription, Object value);
public boolean indexedProperty(Object owner, ClassDescription ownerClassDescription, CollectionPropertyDescription propertyDescription, int index, Object value);

public void unkownOrAmbigousProperty(List<String> path, int pathIndex);
public void invalidProperty(List<String> path, int pathIndex);

}
