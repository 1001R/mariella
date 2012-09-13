package org.mariella.rcp.table.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.mariella.rcp.table.TablePropertyColumnDescriptor;



public abstract class TableColumnEditHandler implements Cloneable {

public interface ModifiableCallback {
boolean canModify(Object element);
}

TablePropertyColumnDescriptor tableColumnDescriptor;
TableViewer tableViewer;
ModifiableCallback modifiableCallback = null;

public TableColumnEditHandler() {
}

public boolean canModify(Object element) {
	if (modifiableCallback == null) return tableColumnDescriptor.canWrite(element);
	return modifiableCallback.canModify(element);
}

public Object implementCellModifierGetValue(Object element) {
	return tableColumnDescriptor.readValue(element);
}

public void implementCellModifierModify(Object element, Object value) {
	if (element instanceof TableItem)
		element = ((TableItem)element).getData();
	tableColumnDescriptor.writeValue(element, value);
	tableViewer.refresh(element);
}

public abstract CellEditor createCellEditor(TableViewerEditController controller);

public TablePropertyColumnDescriptor getTableColumnDescriptor() {
	return tableColumnDescriptor;
}

public void setTableColumnDescriptor(TablePropertyColumnDescriptor tableColumnDescriptor) {
	this.tableColumnDescriptor = tableColumnDescriptor;
}

public TableViewer getTableViewer() {
	return tableViewer;
}

public void setTableViewer(TableViewer tableViewer) {
	this.tableViewer = tableViewer;
}

public ModifiableCallback getModifiableCallback() {
	return modifiableCallback;
}

public void setModifiableCallback(ModifiableCallback modifiableCallback) {
	this.modifiableCallback = modifiableCallback;
}

}
