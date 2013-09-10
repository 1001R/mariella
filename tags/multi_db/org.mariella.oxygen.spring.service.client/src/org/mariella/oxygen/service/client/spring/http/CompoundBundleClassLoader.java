package org.mariella.oxygen.service.client.spring.http;

import java.net.URL;

public class CompoundBundleClassLoader extends ClassLoader {
	
	private final ClassLoader secondary;

	public CompoundBundleClassLoader(ClassLoader parent, ClassLoader secondary) {
		super(parent);
		this.secondary = secondary;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return secondary.loadClass(name);
	}
	
	public URL getResource(final String name) {
		final URL url = super.getResource(name);

		if (null != url) {
			return url;
		}

		return secondary.getResource(name);
	}
	
	public static CompoundBundleClassLoader createFor(ClassLoader ...classLoaders) {
		if (classLoaders.length < 2)
			throw new IllegalArgumentException("This is no foo fighters concert");
		CompoundBundleClassLoader current = null;
		for (int i=classLoaders.length-2; i >= 0; i--) {
			current = new CompoundBundleClassLoader(classLoaders[i], classLoaders[i+1]);
		}
		return current;
	}

}
