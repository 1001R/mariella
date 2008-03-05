package org.mariella.rcp.table.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;

public class TableColumnComboViewerEditHandler extends TableColumnEditHandler {

ILabelProvider labelProvider;
IStructuredContentProvider contentProvider;
ComboViewerCellEditor.Callback callback;

public TableColumnComboViewerEditHandler(IStructuredContentProvider contentProvider, ILabelProvider labelProvider, ComboViewerCellEditor.Callback callback) {
	this.labelProvider = labelProvider;
	this.contentProvider = contentProvider;
	this.callback = callback;
}

public CellEditor createCellEditor(TableViewerEditController controller) {
	ComboViewerCellEditor editor = new ComboViewerCellEditor(controller, contentProvider, labelProvider, callback);
	return editor;
}

}
