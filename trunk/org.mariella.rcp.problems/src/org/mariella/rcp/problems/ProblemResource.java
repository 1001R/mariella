package org.mariella.rcp.problems;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ProblemResource {

private String editorId;
private String elementFactoryId;	// refers to IElementFactory
IEditorInput cachedEditorInput = null;
private IMemento editorMemento;
private ProblemsProvider provider;

public String getEditorId() {
	return editorId;
}

public void setEditorId(String editorId) {
	this.editorId = editorId;
}

public String getElementFactoryId() {
	return elementFactoryId;
}

public void setElementFactoryId(String elementFactoryId) {
	this.elementFactoryId = elementFactoryId;
}

public IMemento getEditorMemento() {
	if (editorMemento == null) {
		Document doc;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		Element el = doc.createElement("root"); //$NON-NLS-1$
		doc.appendChild(el);
		editorMemento = new XMLMemento(doc, el);
	}
	return editorMemento;
}

public void setEditorMemento(IMemento editorMemento) {
	this.editorMemento = editorMemento;
}

public ProblemsProvider getProvider() {
	return provider;
}

public void setProvider(ProblemsProvider provider) {
	this.provider = provider;
}

/**
 * Can be ovverridden if standard open editor behaviour
 * is not enough.
 * 
 * @return
 */
public ProblemResourceOpenHandler getResourceOpenHandler() {
	return null;
}

public abstract String getDescription();

@Override
public abstract boolean equals(Object obj);

@Override
public abstract int hashCode();

}
