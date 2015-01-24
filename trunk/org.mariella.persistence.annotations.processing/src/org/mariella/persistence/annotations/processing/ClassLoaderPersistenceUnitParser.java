package org.mariella.persistence.annotations.processing;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.annotations.processing.ClasspathBrowser.Entry;
import org.mariella.persistence.mapping.OxyUnitInfo;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ClassLoaderPersistenceUnitParser implements PersistenceUnitParser {
	private ClassLoader classLoader = null;
	private List<OxyUnitInfo> oxyUnitInfos = new ArrayList<OxyUnitInfo>();

public ClassLoaderPersistenceUnitParser(ClassLoader classLoader) {
	super();
	this.classLoader = classLoader;
}

@Override
public Class<?> loadClass(Entry entry, String className) throws ClassNotFoundException {
	return classLoader.loadClass(className);
}
	
@Override
public List<Entry> readEntries(OxyUnitInfo oxyUnitInfo) throws Exception {
	return ClasspathBrowser.readEntries(oxyUnitInfo.getPersistenceUnitRootUrl());
}

@Override
public void parsePersistenceUnits() throws Exception {
	URL url = classLoader.getResource(PERSISTENCE_XML_LOCATION);
	if (url == null) {
		throw new Exception("No " + PERSISTENCE_XML_LOCATION + " found.");
	}

	InputStream persistenceXmlIs = url.openStream();
	if (persistenceXmlIs == null)
		throw new Exception("Could not find " + url.toString());

	PersistenceXmlHandler handler = new PersistenceXmlHandler();
	File file=new File(urlDecode(url.getFile()));
	File rootFile = file.getParentFile().getParentFile();
	URL rootUrl;
	if (rootFile.getPath().endsWith("!")) {
		// TODO huuuaaahhhh ... better way to find out if this is path in a jar file?
		// remove '!'
		rootFile = new File(rootFile.getPath().substring(0,rootFile.getPath().length()-1));
		// if in a jar file, the rootFile already contains the "file:" protocol
		rootUrl = new URL(rootFile.getPath());
	} else {
		rootUrl = new URL("file", null, rootFile.getPath());
	}

	XMLReader reader = XMLReaderFactory.createXMLReader();
	reader.setContentHandler(handler);
	reader.parse(new InputSource(persistenceXmlIs));
	
	List<OxyUnitInfo> oxyUnitInfos = handler.getOxyUnitInfos();
	for (OxyUnitInfo unitInfo : oxyUnitInfos) {
		unitInfo.setPersistenceUnitRootUrl(rootUrl);		
	}

	oxyUnitInfos.addAll(oxyUnitInfos);
}

@Override
public List<OxyUnitInfo> getOxyUnitInfos() {
	return oxyUnitInfos;
}

private String urlDecode(String file) throws UnsupportedEncodingException {
	String decoded = URLDecoder.decode(file, "UTF-8");
	return decoded;
}



}
