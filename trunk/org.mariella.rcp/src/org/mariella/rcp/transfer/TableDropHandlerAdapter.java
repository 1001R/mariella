package org.mariella.rcp.transfer;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;

public class TableDropHandlerAdapter extends DropTargetAdapter {

TableViewer tableViewer;
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
	List targetElements = handleDrop(evt.data);
	for (Object targetElement : targetElements) {
		tableViewer.refresh(targetElement);
		handlePostDrop(targetElement, evt.data);
	}
	
	tableViewer.getTable().setFocus();
}

private void handlePostDrop(Object targetElement, Object dropObject) {
	for (TableDropHandler m : handlers) {
		if (m.isResponsibleFor(dropObject))
			m.handlePostDrop(dropObject);
	}
}

private List handleDrop(Object dropObject) {
	for (TableDropHandler m : handlers) {
		if (m.isResponsibleFor(dropObject))
			return m.handleDrop(dropObject);
	}
	return Collections.EMPTY_LIST;
}

public void dragOver(DropTargetEvent evt) {
}

public void dragEnter(DropTargetEvent event) {
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
