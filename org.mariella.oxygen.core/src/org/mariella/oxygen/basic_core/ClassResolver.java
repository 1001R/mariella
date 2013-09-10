package org.mariella.oxygen.basic_core;

public interface ClassResolver {

	Class<?> resolveClass(String className) throws ClassNotFoundException;

}
