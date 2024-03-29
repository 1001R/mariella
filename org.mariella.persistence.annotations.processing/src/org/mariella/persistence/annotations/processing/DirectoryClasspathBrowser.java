package org.mariella.persistence.annotations.processing;

import java.io.File;
import java.io.FileInputStream;

public class DirectoryClasspathBrowser extends ClasspathBrowser {

public DirectoryClasspathBrowser(File dir) {
	read(dir);
}

private void read(File dir) {
	try {
		File[] dirfiles = dir.listFiles();
		if (dirfiles == null)
			return;
		for (int i = 0; i < dirfiles.length; i++) {
			if (dirfiles[i].isDirectory()) {
				read(dirfiles[i]);
			} else {
				if (dirfiles[i].getName().endsWith(".class")) {
					Entry entry = new Entry();
					entry.setName(dirfiles[i].getName());
					entry.setInputStream(new FileInputStream(dirfiles[i]));
					entries.add(entry);
				}
			}
		}
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}

}
