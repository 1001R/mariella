package org.mariella.rcp.transfer;


import java.util.ArrayList;
import java.util.Collection;
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

@Override
public void drop(DropTargetEvent evt) {
	List modifiedElements = handleDrop(evt.data);
	for (Object targetElement : modifiedElements) {
		tableViewer.refresh(targetElement);
	}
	handlePostDrop(modifiedElements);
	
	tableViewer.getTable().setFocus();
}

private void handlePostDrop(List modifiedElements) {
	for (TableDropHandler m : handlers) {
		m.handlePostDrop(modifiedElements);
	}
}

private List handleDrop(Object dropObject) {
	List modifiedElements = new ArrayList();
	Collection<Object> resolvedDropObjects = resolveDropObjects(dropObject);
	for (TableDropHandler m : handlers) {
		for (Object o : resolvedDropObjects)
			if (m.isResponsibleFor(o))
				m.handleDrop(modifiedElements, o);
	}
	return modifiedElements;
}

private Collection<Object> resolveDropObjects(Object dropObject) {
	if (dropObject instanceof Collection) {
		return (Collection)dropObject;
	} else {
		List<Object> objects = new ArrayList<Object>(1);
		objects.add(dropObject);
		return objects;
	}
}

@Override
public void dragOver(DropTargetEvent evt) {
}

@Override
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
