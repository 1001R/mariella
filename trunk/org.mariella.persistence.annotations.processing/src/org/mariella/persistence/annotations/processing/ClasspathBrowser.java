package org.mariella.persistence.annotations.processing;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;

public abstract class ClasspathBrowser {
List<InputStream> entries= new ArrayList<InputStream>();

public static ClasspathBrowser getBrowser(URL url) throws Exception {
	if (url.getProtocol().equals("bundleresource")) {
		url = FileLocator.resolve(url); 
	}
	
	if (url.getProtocol().equals("jar")) {
		String fileName = url.getFile();
		if (fileName.endsWith("!/"))
			fileName = fileName.substring(0,fileName.length()-2);
		if (fileName.startsWith("file:"))
			fileName = fileName.substring(5);
		return new JarClasspathBrowser(new File(fileName));
	} else if (url.getProtocol().equals("file")) {
		File f = new File(url.getFile());
		if (f.isDirectory()) {
			return new DirectoryClasspathBrowser(f);
		} else {
			return new JarClasspathBrowser(f);
		}
	} else {
		throw new IllegalArgumentException("Invalid url: " + url);
	}
}


public List<InputStream> getEntries() {
	return entries;
}

}
