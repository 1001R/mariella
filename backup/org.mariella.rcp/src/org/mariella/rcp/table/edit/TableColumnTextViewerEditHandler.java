package org.mariella.rcp.table.edit;

import org.eclipse.jface.viewers.CellEditor;

public class TableColumnTextViewerEditHandler extends TableColumnEditHandler {

TextViewerCellEditor.Callback callback;

public TableColumnTextViewerEditHandler(TextViewerCellEditor.Callback callback) {
	this.callback = callback;
}

@Override
public CellEditor createCellEditor(TableViewerEditController controller) {
	TextViewerCellEditor editor = new TextViewerCellEditor(controller, callback);
	return editor;
}

}
