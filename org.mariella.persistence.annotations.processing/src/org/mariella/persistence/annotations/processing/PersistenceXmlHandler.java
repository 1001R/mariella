package org.mariella.persistence.annotations.processing;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.mariella.persistence.mapping.OxyUnitInfo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

// TODO validate with schema validator, aquire persistence-schema-xml
public class PersistenceXmlHandler extends DefaultHandler {

private List<OxyUnitInfo> oxyUnitInfos = new ArrayList<OxyUnitInfo>();
private OxyUnitInfo oxyUnitInfo;
private String curValue;

public List<OxyUnitInfo> getOxyUnitInfos() {
	return oxyUnitInfos;
}

public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	try {
		if (localName.equals("persistence-unit")) {
			oxyUnitInfo = new OxyUnitInfo();
			oxyUnitInfos.add(oxyUnitInfo);
			oxyUnitInfo.setPersistenceUnitName(attributes.getValue("name"));
		} if (localName.equals("property")) {
			String name = attributes.getValue("name");
			String value = attributes.getValue("value");
			oxyUnitInfo.getProperties().put(name, value);
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
			oxyUnitInfo.getJarFileUrls().add(url);
		} else if (localName.equals("class")) {
			oxyUnitInfo.getManagedClassNames().add(curValue);
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
