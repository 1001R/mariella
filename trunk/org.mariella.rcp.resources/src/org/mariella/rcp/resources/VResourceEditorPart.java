package org.mariella.rcp.resources;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;

public interface VResourceEditorPart extends IEditorPart {

void firePropertyChange(int prop);

void setSite(IEditorSite site);

void setInput(IEditorInput input);

void setPartName(String name);

VResourceEditorCustomizationCallback createCustomizationCallback();

}
