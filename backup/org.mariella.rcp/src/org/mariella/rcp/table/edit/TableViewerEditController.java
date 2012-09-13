package org.mariella.rcp.table.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.mariella.rcp.table.DefaultStructuredContentProvider;


public class TableViewerEditController {

public interface TabProcessor {
	void handleTraversePreviousOnTableBorder();
	void handleTraverseNextOnTableBorder();
	void handleTraverseDownOnTableBorder(int cellEditorIndex);
}

TableViewer tableViewer;
List<TableViewerEditControllerObserver> observers = new ArrayList<TableViewerEditControllerObserver>();
private boolean handleObservers = false;
TabProcessor tabProcessor;
private TableViewerEditControllerCellEditor curCellEditor;

public void processTabNextEvent(TableViewerEditControllerCellEditor current) {
	if (!primEditNextCell(current, +1)) {
		if (!primEditNextRow(1) && tabProcessor != null)
			tabProcessor.handleTraverseNextOnTableBorder();
	}
}

public void processTabPreviousEvent(TableViewerEditControllerCellEditor current) {
	if (!primEditNextCell(current, -1)) {
		if (!primEditNextRow(-1) && tabProcessor != null)
			tabProcessor.handleTraversePreviousOnTableBorder();
	}
}

public void processCursorDownEvent(TableViewerEditControllerCellEditor current) {
	Object nextRow = getNextRow(1);
	if (nextRow == null) {
		if (tabProcessor != null)
			tabProcessor.handleTraverseDownOnTableBorder(getCellEditorIndex(current));
	} else {
		tableViewer.setSelection(new StructuredSelection(nextRow));
		editCell(getCellEditorIndex(current));
	}
}

public void processCursorUpEvent(TableViewerEditControllerCellEditor current) {
	Object nextRow = getNextRow(-1);
	if (nextRow != null) {
		tableViewer.setSelection(new StructuredSelection(nextRow));
		editCell(getCellEditorIndex(current));
	}
}

private boolean primEditNextRow(int direction) {
	Object nextRow = getNextRow(direction); 
	if (nextRow != null) {
		tableViewer.setSelection(new StructuredSelection(nextRow));
		if (direction == 1)
			editFirstCell();
		else if (direction == -1)
			editLastCell();
		return true;
	}
	return false;
}

private Object getNextRow(int direction) {
	int curRow = tableViewer.getTable().getSelectionIndex();
	List elements = (List)tableViewer.getInput();
	if (direction == 1 && curRow < elements.size()-1) {
		return elements.get(curRow+1);
	}
	else if (direction == -1 && curRow > 0) {
		return elements.get(curRow-1);
	}
	return null;
}

private boolean primEditNextCell(TableViewerEditControllerCellEditor current, int direction) {
	int index = getCellEditorIndex(current);
	Object selectedElement = getSelectedElement();
	if (selectedElement != null) {
		int nextIndex = getNextEditableColumn(index, direction);
		if (nextIndex != -1) {
			tableViewer.editElement(selectedElement, nextIndex);
			tableViewer.getCellEditors()[index].setFocus();
			return true;
		}
	}
	return false;
}

public void editFirstCell() {
	Object selectedElement = getSelectedElement();
	if (selectedElement != null) {
		int index = getNextEditableColumn(-1, +1);
		if (index != -1) {
			tableViewer.editElement(selectedElement, index);
			tableViewer.getCellEditors()[index].setFocus();
		}
	}	
}

public void editLastCell() {
	Object selectedElement = getSelectedElement();
	if (selectedElement != null) {
		int index = getNextEditableColumn(tableViewer.getCellEditors().length, -1);
		if (index != -1) {
			tableViewer.editElement(selectedElement, index);
			tableViewer.getCellEditors()[index].setFocus();
		}
	}	
}

public void editCell(int columnIndex) {
	Object selectedElement = getSelectedElement();
	tableViewer.editElement(selectedElement, columnIndex);
	tableViewer.getCellEditors()[columnIndex].setFocus();
}

private int getNextEditableColumn(int index, int direction) {
	Object selectedElement = getSelectedElement();
	CellEditor[] editors = tableViewer.getCellEditors();
	for (int i=index+direction; i<editors.length && i >= 0; i += direction) {
		if (editors[i] != null && canEdit(selectedElement, i))
			return i;
	}
	return -1;
}

private boolean canEdit(Object element, int index) {
	return tableViewer.getCellModifier().canModify(element, (String) tableViewer.getColumnProperties()[index]);
}

private int getCellEditorIndex(TableViewerEditControllerCellEditor current) {
	int index = 0;
	for (CellEditor e : tableViewer.getCellEditors()) {
		if (e == current)
			return index;
		index++;
	}
	throw new IllegalArgumentException();
}

protected Object getSelectedElement() {
	return ((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
}

public void add(Object newElement) {
	tableViewer.add(newElement);
	((List)tableViewer.getInput()).add(newElement);
	fireContentModified();
}

public void add(int index, Object newElement) {
	tableViewer.insert(newElement, index);
	((List)tableViewer.getInput()).add(index, newElement);
	fireContentModified();
}

public void remove(Object element) {
	if (curCellEditor != null)
		curCellEditor.deactivate();
	tableViewer.remove(element);
	((List)tableViewer.getInput()).remove(element);
	fireContentModified();
}

public void addObserver(TableViewerEditControllerObserver observer) {
	observers.add(observer);
}

public void fireContentModified() {
	if (!handleObservers) return;
	for (TableViewerEditControllerObserver obs : observers)
		obs.contentModified();
}

public void initialize() {
	((DefaultStructuredContentProvider)tableViewer.getContentProvider()).setEditController(this);
	tableViewer.getTable().addMouseListener(new MouseAdapter() {
		@Override
		public void mouseDoubleClick(MouseEvent e) {
			if (curCellEditor != null)
				curCellEditor.setEditMode(true);
		}
	});
	/*
	TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewer ,new FocusCellOwnerDrawHighlighter(tableViewer));
	ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
		protected boolean isEditorActivationEvent(
				ColumnViewerEditorActivationEvent event) {
			return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
					|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
					|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
					|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
		}
	};
	
	TableViewerEditor.create(tableViewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
			| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
			| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
			*/	
}

public boolean isHandleObservers() {
	return handleObservers;
}

public void setHandleObservers(boolean handleObservers) {
	this.handleObservers = handleObservers;
}

public TabProcessor getTabProcessor() {
	return tabProcessor;
}

public void setTabProcessor(TabProcessor tabProcessor) {
	this.tabProcessor = tabProcessor;
}

public TableViewer getTableViewer() {
	return tableViewer;
}

public void setTableViewer(TableViewer tableViewer) {
	this.tableViewer = tableViewer;
}

public void activated(TableViewerEditControllerCellEditor cellEditor) {
	curCellEditor = cellEditor;
}

public void deactivated(TableViewerEditControllerCellEditor tableViewerEditControllerCellEditor) {
	curCellEditor = null;
}

public TableViewerEditControllerCellEditor getCurCellEditor() {
	return curCellEditor;
}

public void setEditable(boolean editable) {
	if (!editable && curCellEditor != null)
		curCellEditor.deactivate();
	((DefaultCellModifier)tableViewer.getCellModifier()).setEditable(editable);
}

}
