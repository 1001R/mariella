package org.mariella.persistence.annotations.processing;

import java.util.List;

import org.mariella.persistence.annotations.processing.ClasspathBrowser.Entry;
import org.mariella.persistence.mapping.OxyUnitInfo;

public interface PersistenceUnitParser {
	public final static String PERSISTENCE_XML_LOCATION = "META-INF/persistence.xml";
	
public List<OxyUnitInfo> getOxyUnitInfos();
public List<Entry> readEntries(OxyUnitInfo oxyUnitInfo) throws Exception;
public void parsePersistenceUnits() throws Exception;
public Class<?> loadClass(Entry entry, String className) throws ClassNotFoundException;

}