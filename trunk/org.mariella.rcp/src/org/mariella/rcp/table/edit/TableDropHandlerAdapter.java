package org.mariella.rcp.table.edit;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableItem;

public class TableDropHandlerAdapter extends DropTargetAdapter {

TableViewer tableViewer;
Object targetElement;
List<TableDropHandler> handlers = new ArrayList<TableDropHandler>();

public TableDropHandlerAdapter(TableViewer tableViewer) {
	this.tableViewer = tableViewer;
}

public TableDropHandlerAdapter(TableViewer tableViewer, TableDropHandler ... handlers) {
	this.tableViewer = tableViewer;
	for (TableDropHandler h : handlers)
		addHandler(h);
}

public void drop(DropTargetEvent evt) {
	if (targetElement != null)
		tableViewer.setSelection(new StructuredSelection(targetElement));
	else {
		targetElement = handleDropElementOnEmptySelection(evt.data);
		if (targetElement == null) return;
	}

	handleDrop(targetElement, evt.data);
	
	tableViewer.refresh(targetElement);
	
	handlePostDrop(targetElement, evt.data);
	
	targetElement = null;
	
	tableViewer.getTable().setFocus();
}

private void handlePostDrop(Object targetElement, Object dropObject) {
	for (TableDropHandler m : handlers) {
		if (m.isResponsibleFor(targetElement, dropObject))
			m.handlePostDrop(targetElement, dropObject);
	}
}

private void handleDrop(Object targetElement, Object dropObject) {
	for (TableDropHandler m : handlers) {
		if (m.isResponsibleFor(targetElement, dropObject))
			m.handleDrop(targetElement, dropObject);
	}
}

private Object handleDropElementOnEmptySelection(Object dropObject) {
	for (TableDropHandler m : handlers) {
		Object newElement = m.handleDropElementOnEmptySelection(dropObject);
		if (newElement != null) return newElement;
	}
	return null;
}

public void dragOver(DropTargetEvent evt) {
	targetElement = null;
	Point point = tableViewer.getTable().toControl(evt.x, evt.y);
	TableItem item = tableViewer.getTable().getItem(point);
	if (item != null)
		targetElement = item.getData();
}

public void dragEnter(DropTargetEvent event) {
	targetElement = null;
}

public void addHandler(TableDropHandler handler) {
	this.handlers.add(handler);
}

public void addDropSupport() {
	Transfer[] transfers = collectHandlerTransfers();
	tableViewer.addDropSupport(DND.DROP_MOVE, 
			transfers, 
			this);

}

private Transfer[] collectHandlerTransfers() {
	Set<Transfer> transfers = new HashSet<Transfer>();
	for (TableDropHandler m : handlers)
		m.collectTransfers(transfers);
	
	Transfer[] array = new Transfer[transfers.size()];
	transfers.toArray(array);
	return array;
}

}
