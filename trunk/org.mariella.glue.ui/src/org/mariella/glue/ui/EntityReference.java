package org.mariella.glue.ui;

public class EntityReference {
	private String className;
	private Object identity;
	
public EntityReference(String className, Object identity) {
	super();
	this.className = className;
	this.identity = identity;
}

public EntityReference() {
	super();
}
	
public String getClassName() {
	return className;
}

public void setClassName(String className) {
	this.className = className;
}

public Object getIdentity() {
	return identity;
}

public void setIdentity(Object identity) {
	this.identity = identity;
}

@Override
public boolean equals(Object obj) {
	if(obj instanceof EntityReference) {
		EntityReference er = (EntityReference)obj;
		return className.equals(er.className) && identity.equals(er.identity);
	} else {
		return false;
	}
} 

@Override
public int hashCode() {
	return identity.hashCode();
}

}
