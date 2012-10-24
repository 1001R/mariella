package org.mariella.oxygen.runtime.impl;

import org.mariella.oxygen.basic_core.ClassResolver;
import org.mariella.persistence.loader.ModifiableFactory;
import org.mariella.persistence.schema.ClassDescription;

public class OxyModifiableFactory implements ModifiableFactory {

	private final ClassResolver classResolver;

	public OxyModifiableFactory(ClassResolver classResolver) {
		this.classResolver = classResolver;
	}

	public ClassResolver getClassResolver() {
		return classResolver;
	}

	public Object createModifiable(ClassDescription classDescription) {
		try {
			return getClassResolver().resolveClass(classDescription.getClassName()).newInstance();
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
			return getClassResolver().resolveClass(classDescription.getClassName()).newInstance();
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch(InstantiationException e) {
			throw new RuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
