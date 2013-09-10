package org.mariella.persistence.annotations.processing;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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



public static List<Entry> readEntries(URL url) {
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

public List<Entry> getEntries() {
	return entries;
}

}
