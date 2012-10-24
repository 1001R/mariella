package org.mariella.oxygen.basic_impl;

import org.mariella.oxygen.basic_core.ClassResolver;

public class DefaultClassResolver implements ClassResolver {

	private final ClassLoader classLoader;

	public DefaultClassResolver(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public Class<?> resolveClass(String className)
			throws ClassNotFoundException {
		return classLoader.loadClass(className);
	}

}
