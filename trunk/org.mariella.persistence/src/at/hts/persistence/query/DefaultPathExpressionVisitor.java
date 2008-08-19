package at.hts.persistence.query;

import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;

public abstract class DefaultPathExpressionVisitor implements PathExpressionVisitor {

public void afterEnd(String token) {
	throw new IllegalStateException("after end!");
}

public void property(ClassDescription classDescription, PropertyDescription propertyDescription) {
}

public abstract ClassDescription root(String token);

public void unknownProperty(ClassDescription classDescription, String propertyName) {
	throw new IllegalStateException("unkown property: " + propertyName + " for class " + classDescription.getClassName());
}

}
