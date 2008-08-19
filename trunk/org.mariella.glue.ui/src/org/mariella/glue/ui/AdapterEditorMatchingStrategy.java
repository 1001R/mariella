package org.mariella.glue.ui;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

public class AdapterEditorMatchingStrategy implements IEditorMatchingStrategy {

public boolean matches(IEditorReference editorRef, IEditorInput input) {
	if (!(input instanceof EntityAdapter)) return false;
	try {
		return ((EntityAdapter<?>)editorRef.getEditorInput()).getRef().equals(((EntityAdapter<?>)input).getRef());
	} catch (PartInitException e) {
		throw new RuntimeException(e);
	}
}
}
