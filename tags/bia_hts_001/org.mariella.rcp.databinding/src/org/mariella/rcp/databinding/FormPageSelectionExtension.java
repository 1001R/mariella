package org.mariella.rcp.databinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.forms.editor.FormEditor;
import org.mariella.rcp.databinding.internal.InternalBindingContext;
import org.mariella.rcp.databinding.internal.VDataBindingSelectionDispatchContext;

public class FormPageSelectionExtension extends ContextSelectionManagementExtension {
static Log log = LogFactory.getLog(FormPageSelectionExtension.class);

FormEditor formEditor;

public FormPageSelectionExtension(FormEditor formEditor) {
	this.formEditor = formEditor;
}

@Override
public void install(VBindingContext bindingContext) {
	super.install(bindingContext);
	((InternalBindingContext)bindingContext).getMainContext().setActionBars(formEditor.getEditorSite().getActionBars());
}

@Override
public VBindingSelection completeSelectionPath(VBindingSelection selection) {
	if (formEditor != null && formEditor.getActivePageInstance() == null) return null;
	String pageId = formEditor.getActivePageInstance().getId();
	Object[] elements = selection.toArray();
	SelectionPath[] newPathes = new SelectionPath[elements.length];
	for (int i=0; i<elements.length; i++) {
		SelectionPath path = (SelectionPath)elements[i];
		newPathes[i] = new SelectionPath(new Object[]{pageId},path.qualifiers);
	}
	return new VBindingSelection(selection.getTargetObservable(), selection.origin, newPathes);
}

@Override
public void dispatchSelection(VDataBindingSelectionDispatchContext dispatchCtx) {
	Object nextPathToken = dispatchCtx.nextPathToken();
	if (nextPathToken == null) return;
	
	if (!(nextPathToken instanceof String)) {
		if (log.isDebugEnabled()) log.debug("Cannot dispatch selection because path token is not a valid page id (not a String): " + nextPathToken);
		return;
	}
	String newPageId = (String)nextPathToken;
	boolean pageChange = !formEditor.getActivePageInstance().getId().equals(newPageId);
	if (pageChange) {
		if (formEditor.setActivePage(newPageId) == null) {
			if (log.isDebugEnabled()) log.debug("Cannot dispatch selection because there is no page having the id: " + newPageId);
			return;
		}
	}
	dispatchCtx.invokeNextDispatcher(pageChange);
}

public FormEditor getFormEditor() {
	return formEditor;
}

@Override
public void dispose() {}

}