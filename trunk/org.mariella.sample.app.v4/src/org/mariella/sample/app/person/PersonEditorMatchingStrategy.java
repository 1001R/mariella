package org.mariella.sample.app.person;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

public class PersonEditorMatchingStrategy implements IEditorMatchingStrategy {

@Override
public boolean matches(IEditorReference editorRef, IEditorInput input) {
	if (!(input instanceof PersonResource)) return false;
	try {
		return ((PersonResource)editorRef.getEditorInput()).getRef().equals(((PersonResource)input).getRef());
	} catch (PartInitException e) {
		throw new RuntimeException(e);
	}
}

}
