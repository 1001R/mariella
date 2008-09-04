package org.mariella.rcp.problems;

import org.eclipse.ui.IMemento;

public interface EditorProblemResource extends ProblemResource {

String getEditorId();

void setEditorId(String editorId);

String getElementFactoryId();

void setElementFactoryId(String elementFactoryId);

IMemento getEditorMemento();

void setEditorMemento(IMemento editorMemento);

}
