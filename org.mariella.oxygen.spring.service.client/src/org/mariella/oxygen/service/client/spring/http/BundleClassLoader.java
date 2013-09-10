package org.mariella.oxygen.service.client.spring.http;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.osgi.framework.Bundle;
 
/**
 * A ClassLoader delegating to a given OSGi bundle.
 *
 * @version $Rev: 896324 $, $Date: 2010-01-06 07:05:04 +0100 (Wed, 06 Jan 2010) $
 */
public class BundleClassLoader extends ClassLoader {
 
    private final Bundle bundle;
 
    public BundleClassLoader(Bundle bundle) {
        this.bundle = bundle;
    }
 
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return bundle.loadClass(name);
    }
 
    protected URL findResource(String name) {
        URL resource = bundle.getResource(name);
       return resource;
    }
 
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Enumeration findResources(String name) throws IOException {
        return bundle.getResources(name);
    }
 
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        @SuppressWarnings("rawtypes")
		Class clazz = findClass(name);
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
 
    public Bundle getBundle() {
        return bundle;
    }
}