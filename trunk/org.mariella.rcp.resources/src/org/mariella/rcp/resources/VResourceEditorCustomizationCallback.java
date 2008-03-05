package org.mariella.rcp.resources;

/**
 * Decouples the eclipse api calls from the customizing calls.  
 * 
 * @author maschmid
 *
 */
public interface VResourceEditorCustomizationCallback {

AbstractVResourceManager getResourceManager();

VResource getResource();

void implementRefresh(boolean layout);

void implementInit();

void implementDispose();

void aboutToCloseEditor();

void activated();

void deactivated();

void implementSetFocus();

void aboutToSave();

void setResourceEditorPart(VResourceEditorPart editorPart);

Object createCustomEditingContext();
}
