package org.mariella.persistence.annotations.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

public abstract class ClasspathBrowser {
	public static class Entry {
		private String name;
		private InputStream inputStream;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public InputStream getInputStream() {
			return inputStream;
		}
		public void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}
	}

List<Entry> entries= new ArrayList<Entry>();

public static ClasspathBrowser getBrowser(URL url, Bundle bundle) throws Exception {

	if (url.getProtocol().equals("bundleresource")) {
		if(bundle == null) {
			throw new IllegalArgumentException("Cannot resolve a bundle url without a bundle!");
		}
		url = FileLocator.getBundleFile(bundle).toURI().toURL();
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

public static List<Entry> resolveEntries(URL url, Bundle bundle) throws Exception {
	if (url.getProtocol().equals("bundleresource")) {
		final List<Entry> entries = new ArrayList<Entry>();
		if(bundle == null) {
			throw new IllegalArgumentException("Cannot resolve a bundle url without a bundle!");
		}
		for(File bcpEntry : getBundleClasspathEntries(bundle)) {
			entries.addAll(readEntries(bcpEntry.toURI().toURL()));
		}
		return entries;
	} else {
		return readEntries(url);
	}
}

private static List<Entry> readEntries(URL url) {
	if (url.getProtocol().equals("jar")) {
		String fileName = url.getFile();
		if (fileName.endsWith("!/")) {
			fileName = fileName.substring(0,fileName.length()-2);
		}
		if (fileName.startsWith("file:")) {
			fileName = fileName.substring(5);
		}
		return new JarClasspathBrowser(new File(fileName)).entries;
	} else if (url.getProtocol().equals("file")) {
		String fName = toFileName(url);
		File f = new File(fName);
		if (f.isDirectory()) {
			return new DirectoryClasspathBrowser(f).entries;
		} else {
			return new JarClasspathBrowser(f).entries;
		}
	} else {
		throw new IllegalArgumentException("Invalid url: " + url);
	}
}

private static String toFileName(URL url) {
	String fName = url.getFile();
	try {
		fName = URLDecoder.decode( fName, "UTF-8");
	} catch (UnsupportedEncodingException e) {
		throw new RuntimeException(e);
	}
	return fName;
}

public static List<File> getBundleClasspathEntries(Bundle bundle) throws IOException {
	final List<File> classpathFiles = new ArrayList<File>();
	File bundleFile = FileLocator.getBundleFile(bundle);
	String bundlePath = bundleFile.getAbsolutePath();
	String bundleClasspath = (String) bundle.getHeaders().get("Bundle-Classpath");
	if(bundleClasspath != null) {
		String[] paths = bundleClasspath.split(",");
		File classpathFile;
		for(String path : paths) {
			if(!(path = path.trim()).isEmpty()) {
				if(!path.startsWith(File.separator)) {
					classpathFile = new File(bundlePath + File.separator + path).getCanonicalFile();
					if(classpathFile.exists()) {
						classpathFiles.add(classpathFile);
					}
				}
			}
		}
	}
	if(classpathFiles.isEmpty()) {
		classpathFiles.add(bundleFile);
	}
	return classpathFiles;
}


public List<Entry> getEntries() {
	return entries;
}

}
