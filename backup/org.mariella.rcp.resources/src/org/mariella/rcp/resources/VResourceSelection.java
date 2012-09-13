package org.mariella.rcp.resources;

import java.util.List;

import org.eclipse.ui.IWorkbenchPart;

public class VResourceSelection {

IWorkbenchPart part;
List<VManagedSelectionItem> selectedItems;

public VResourceSelection(IWorkbenchPart part, List<VManagedSelectionItem> selectedItems) {
	this.part = part;
	this.selectedItems = selectedItems;
}

public IWorkbenchPart getPart() {
	return part;
}

public List<VManagedSelectionItem> getSelectedItems() {
	return selectedItems;
}

public boolean isEmpty() {
	return selectedItems.size() == 0;
}

public VManagedSelectionItem getFirstItem() {
	return selectedItems.size() == 0 ? null : selectedItems.get(0);
}

}
