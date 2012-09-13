package org.mariella.persistence.annotations.processing;

import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// TODO validate with schema validator, aquire persistence-schema-xml
public class PersistenceXmlHandler extends DefaultHandler {

OxyUnitInfo oxyUnitInfo;
String curValue;


public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	try {
		if (localName.equals("persistence-unit")) {
			oxyUnitInfo.persistenceUnitName = attributes.getValue("name");
		} if (localName.equals("property")) {
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			oxyUnitInfo.properties.put(name, value);
		}
	} catch (Exception e) {
		e.printStackTrace();
		throw new SAXException(e);
	}
}

@Override
public void endElement(String uri, String localName, String qName) throws SAXException {
	try {
		if (localName.equals("jar-file")) {
			URL url = new URL(curValue); 
			oxyUnitInfo.jarFileUrls.add(url);
		} else if (localName.equals("class")) {
			oxyUnitInfo.managedClassNames.add(curValue);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
		throw new SAXException(e);
	}
}

@Override
public void characters(char[] ch, int start, int length) throws SAXException {
	curValue = new String(ch, start, length);
}

public OxyUnitInfo getOxyUnitInfo() {
	return oxyUnitInfo;
}

}
