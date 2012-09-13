package org.mariella.rcp.table;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.mariella.rcp.table.edit.TableViewerEditController;


public class DefaultStructuredContentProvider implements IStructuredContentProvider {

TableViewerEditController editController;

@SuppressWarnings("unchecked")
public Object[] getElements(Object input) {
	return ((List)input).toArray();
}

public void dispose() {}

public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	if (editController != null) {
		editController.fireContentModified();
	}
}

public TableViewerEditController getEditController() {
	return editController;
}

public void setEditController(TableViewerEditController editController) {
	this.editController = editController;
}

}
