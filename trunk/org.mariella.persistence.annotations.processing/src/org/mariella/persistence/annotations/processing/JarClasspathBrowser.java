package org.mariella.persistence.annotations.processing;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("unchecked")
public class JarClasspathBrowser extends ClasspathBrowser {
ZipFile zip;
File file;

public JarClasspathBrowser(File f) {
	this.file = f;
	read();
}

private void read() {
	try {
		zip = new ZipFile(file);
		for (Enumeration zipentries = zip.entries(); zipentries.hasMoreElements();) {
			ZipEntry zentry = (ZipEntry)zipentries.nextElement();
			if (zentry.isDirectory()) continue;
			if (!zentry.getName().endsWith(".class")) continue;

			entries.add(zip.getInputStream(zentry));
		}
	} catch (IOException e) {
		throw new RuntimeException(e);
	}
}
}
