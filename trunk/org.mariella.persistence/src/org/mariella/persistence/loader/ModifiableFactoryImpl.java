package org.mariella.persistence.loader;

import org.mariella.persistence.runtime.Modifiable;
import org.mariella.persistence.schema.ClassDescription;


public class ModifiableFactoryImpl implements ModifiableFactory {
	private ClassLoader classLoader;
	
public ModifiableFactoryImpl() {
	super();
}

public ModifiableFactoryImpl(ClassLoader classLoader) {
	super();
	this.classLoader = classLoader;
}

public Modifiable createModifiable(ClassDescription classDescription) {
	try {
		if(classLoader == null) {
			return (Modifiable)Class.forName(classDescription.getClassName()).newInstance();
		} else {
			return (Modifiable)classLoader.loadClass(classDescription.getClassName()).newInstance();
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
