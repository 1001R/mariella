package org.mariella.persistence.loader;

import org.mariella.persistence.schema.ClassDescription;


public class ModifiableFactoryImpl implements ModifiableFactory {
	private final ClassLoader classLoader;
	
public ModifiableFactoryImpl() {
	super();
	classLoader = null;
}

public ModifiableFactoryImpl(ClassLoader classLoader) {
	super();
	this.classLoader = classLoader;
}

public Object createModifiable(ClassDescription classDescription) {
	try {
		if(classLoader == null) {
			return (Object)Class.forName(classDescription.getClassName()).newInstance();
		} else {
			return (Object)classLoader.loadClass(classDescription.getClassName()).newInstance();
		}
	} catch(ClassNotFoundException e) {
		throw new RuntimeException(e);
	} catch(InstantiationException e) {
		throw new RuntimeException(e);
	} catch(IllegalAccessException e) {
		throw new RuntimeException(e);
	}
}

public Object createEmbeddable(ClassDescription classDescription) {
	try {
		if(classLoader == null) {
			return Class.forName(classDescription.getClassName()).newInstance();
		} else {
			return classLoader.loadClass(classDescription.getClassName()).newInstance();
		}
	} catch(ClassNotFoundException e) {
		throw new RuntimeException(e);
	} catch(InstantiationException e) {
		throw new RuntimeException(e);
	} catch(IllegalAccessException e) {
		throw new RuntimeException(e);
	}
}

}
