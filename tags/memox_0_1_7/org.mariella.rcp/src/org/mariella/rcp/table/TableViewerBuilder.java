package org.mariella.rcp.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.mariella.rcp.table.edit.DefaultCellModifier;
import org.mariella.rcp.table.edit.TableViewerEditController;


public class TableViewerBuilder {

TableDescriptor tableDescriptor;
TableViewer tableViewer;
ITableLabelProvider labelProvider;

public TableViewerBuilder(TableDescriptor tableDescriptor, TableViewer tableViewer) {
	this(tableDescriptor, tableViewer, new DefaultTableLabelProvider(tableDescriptor));
}

public TableViewerBuilder(TableDescriptor tableDescriptor, TableViewer tableViewer, ITableLabelProvider labelProvider) {
	this.tableDescriptor = tableDescriptor;
	this.tableViewer = tableViewer;
	this.labelProvider = labelProvider;
}


public void buildTableViewer() {
	TableLayout layout = new TableLayout();
	tableViewer.getTable().setLayout(layout);
	tableViewer.getTable().setHeaderVisible(true);
	for (TableColumnDescriptor colDescr : tableDescriptor.getColumnDescriptors()) {
		layout.addColumnData(new ColumnWeightData(colDescr.getWeight(), 30, true));
		
		TableColumn col = new TableColumn(tableViewer.getTable(), SWT.NONE);
		col.setText(colDescr.getHeader());
	}
	
	tableViewer.setContentProvider(new DefaultStructuredContentProvider());
	tableViewer.setLabelProvider(labelProvider);
	tableViewer.setColumnProperties(getColumnProperties());
	tableViewer.setCellModifier(new DefaultCellModifier(tableDescriptor));
}


public TableViewerEditController buildTableViewerEditController() {
	TableViewerEditController controller = new TableViewerEditController();
	controller.setTableViewer(tableViewer);
	controller.getTableViewer().setCellEditors(buildCellEditors(controller));
	controller.initialize();
	return controller;
}

private CellEditor[] buildCellEditors(TableViewerEditController controller) {
	CellEditor[] cellEditors = new CellEditor[tableDescriptor.getColumnDescriptors().length];
	int index = 0;
	for (TableColumnDescriptor colDescr : tableDescriptor.getColumnDescriptors()) {
		if (!(colDescr instanceof TablePropertyColumnDescriptor)) continue;
		
		if (((TablePropertyColumnDescriptor)colDescr).editHandler == null) {
			cellEditors[index++] = null;
			continue;
		}
		cellEditors[index++] = ((TablePropertyColumnDescriptor)colDescr).editHandler.createCellEditor(controller);
		((TablePropertyColumnDescriptor)colDescr).editHandler.setTableViewer(tableViewer);
	}
	return cellEditors;
}

private String[] getColumnProperties() {
	String[] props = new String[tableDescriptor.getColumnDescriptors().length];
	int index = 0;
	for (TableColumnDescriptor colDescr : tableDescriptor.getColumnDescriptors()) {
		if (!(colDescr instanceof TablePropertyColumnDescriptor)) {
			props[index++] = null;
		} else {
			props[index++] = ((TablePropertyColumnDescriptor)colDescr).getPropertyPath();
		}
	}
	return props;
}


}
