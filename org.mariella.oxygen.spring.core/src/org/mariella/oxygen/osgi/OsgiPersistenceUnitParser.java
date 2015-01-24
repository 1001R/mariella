package org.mariella.oxygen.osgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.mariella.persistence.annotations.processing.ClasspathBrowser;
import org.mariella.persistence.annotations.processing.ClasspathBrowser.Entry;
import org.mariella.persistence.annotations.processing.DirectoryClasspathBrowser;
import org.mariella.persistence.annotations.processing.JarClasspathBrowser;
import org.mariella.persistence.annotations.processing.PersistenceUnitParser;
import org.mariella.persistence.annotations.processing.PersistenceXmlHandler;
import org.mariella.persistence.mapping.OxyUnitInfo;
import org.osgi.framework.Bundle;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class OsgiPersistenceUnitParser implements PersistenceUnitParser {
	public static class BundleEntry extends ClasspathBrowser.Entry {
		private Bundle bundle;
		public Bundle getBundle() {
			return bundle;
		}
		public void setBundle(Bundle bundle) {
			this.bundle = bundle;
		}
	};
	
	private List<Bundle> bundles = null;
	private List<OxyUnitInfo> oxyUnitInfos = new ArrayList<OxyUnitInfo>();
	
@Override
public List<Entry> readEntries(OxyUnitInfo oxyUnitInfo) throws Exception {
	return (List<Entry>)(List<?>)resolveBundleEntries(bundles);	
}

@Override
public void parsePersistenceUnits() throws Exception {
	List<URL> urls = new ArrayList<URL>();
	for (Bundle bundle : getBundles()) {
		URL url = bundle.getResource(PERSISTENCE_XML_LOCATION);
		if (url != null) {
			urls.add(url);
		}
	} 
	if (urls.isEmpty()) {
		throw new Exception("No " + PERSISTENCE_XML_LOCATION + " found.");
	}
	for (URL url : urls) {
		oxyUnitInfos.addAll(parsePersistenceUnit(url));
	}
}

@Override
public Class<?> loadClass(Entry entry, String className) throws ClassNotFoundException {
	return ((BundleEntry)entry).getBundle().loadClass(className);
}

@Override
public List<OxyUnitInfo> getOxyUnitInfos() {
	return oxyUnitInfos;
}

private List<OxyUnitInfo> parsePersistenceUnit(URL xmlRes) throws Exception {
	InputStream persistenceXmlIs = xmlRes.openStream();
	if (persistenceXmlIs == null)
		throw new Exception("Could not find " + xmlRes.toString());

	PersistenceXmlHandler handler = new PersistenceXmlHandler();
	XMLReader reader = XMLReaderFactory.createXMLReader();
	reader.setContentHandler(handler);
	reader.parse(new InputSource(persistenceXmlIs));

	return handler.getOxyUnitInfos();
}

public List<Bundle> getBundles() {
	if (bundles == null) {
		bundles = new ArrayList<Bundle>(5);
		IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor("org.mariella.persistence.persistenceBundles");
		for (IConfigurationElement configElement : configElements) {
			if (configElement.getName().equals("bundle")) {
				String bundleId = configElement.getAttribute("bundleId");
				Bundle bundle = Platform.getBundle(bundleId);
				if (bundle == null) {
					// boom
				}
				bundles.add(bundle);
			}
		}
	}
	return bundles;
}

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
public static List<BundleEntry> resolveBundleEntries(List<Bundle> bundles) throws Exception {
	final List<BundleEntry> entries = new ArrayList<BundleEntry>();
	for (Bundle bundle : bundles) {
		for(File bcpEntry : getBundleClasspathEntries(bundle)) {
			List<BundleEntry> bundleEntries = readEntries(bcpEntry.toURI().toURL(), bundle);
			entries.addAll(bundleEntries);
		}
	}
	return entries;
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

public static List<BundleEntry> readEntries(URL url, Bundle bundle) {
	List<Entry> entries = ClasspathBrowser.readEntries(url);
	List<BundleEntry> result = new ArrayList<BundleEntry>();
	for (Entry entry : entries) {
		BundleEntry bundleEntry = new BundleEntry();
		bundleEntry.setName(entry.getName());
		bundleEntry.setInputStream(entry.getInputStream());
		bundleEntry.setBundle(bundle);
		result.add(bundleEntry);
	}
	return result;
	
}


}
