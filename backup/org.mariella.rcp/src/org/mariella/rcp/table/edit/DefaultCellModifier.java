package org.mariella.rcp.table.edit;

import org.eclipse.jface.viewers.ICellModifier;
import org.mariella.rcp.table.TableDescriptor;
import org.mariella.rcp.table.TablePropertyColumnDescriptor;


public class DefaultCellModifier implements ICellModifier {

TableDescriptor tableDescriptor;
boolean editable = true;

public DefaultCellModifier(TableDescriptor tableDescriptor) {
	this.tableDescriptor = tableDescriptor;
}

public boolean canModify(Object element, String property) {
	if (!editable) return false;
	TablePropertyColumnDescriptor colDescr = tableDescriptor.getColumnDescriptor(property);
	if (colDescr == null) return false;
	if (colDescr.getEditHandler() == null) return false;
	return colDescr.getEditHandler().canModify(element);
}

public Object getValue(Object element, String property) {
	TablePropertyColumnDescriptor colDescr = tableDescriptor.getColumnDescriptor(property);
	if (colDescr == null) throw new IllegalStateException();
	return colDescr.getEditHandler().implementCellModifierGetValue(element);
}

public void modify(Object element, String property, Object value) {
	TablePropertyColumnDescriptor colDescr = tableDescriptor.getColumnDescriptor(property);
	if (colDescr == null) throw new IllegalStateException();
	colDescr.getEditHandler().implementCellModifierModify(element, value);
}

public boolean isEditable() {
	return editable;
}

public void setEditable(boolean editable) {
	this.editable = editable;
}

}
