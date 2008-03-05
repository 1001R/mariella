package org.mariella.rcp.transfer;


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

/**
 * Used for drop mechanisms where either a row is the target or
 * a new row should be created if the drop position is not over a row. 
 * 
 * @author martin
 *
 */
public class TableOnRowDropHandlerAdapter extends DropTargetAdapter {

TableViewer tableViewer;
Object targetElement;
List<TableOnRowDropHandler> handlers = new ArrayList<TableOnRowDropHandler>();

public TableOnRowDropHandlerAdapter(TableViewer tableViewer) {
	this.tableViewer = tableViewer;
}

public TableOnRowDropHandlerAdapter(TableViewer tableViewer, TableOnRowDropHandler ... handlers) {
	this.tableViewer = tableViewer;
	for (TableOnRowDropHandler h : handlers)
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
	for (TableOnRowDropHandler m : handlers) {
		if (m.isResponsibleFor(targetElement, dropObject))
			m.handlePostDrop(targetElement, dropObject);
	}
}

private void handleDrop(Object targetElement, Object dropObject) {
	for (TableOnRowDropHandler m : handlers) {
		if (m.isResponsibleFor(targetElement, dropObject))
			m.handleDrop(targetElement, dropObject);
	}
}

private Object handleDropElementOnEmptySelection(Object dropObject) {
	for (TableOnRowDropHandler m : handlers) {
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

public void addHandler(TableOnRowDropHandler handler) {
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
	for (TableOnRowDropHandler m : handlers)
		m.collectTransfers(transfers);
	
	Transfer[] array = new Transfer[transfers.size()];
	transfers.toArray(array);
	return array;
}

}
