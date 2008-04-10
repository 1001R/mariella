package org.mariella.rcp.table.edit;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ComboViewerCellEditor extends TableViewerEditControllerCellEditor {
public interface Callback extends TableViewerEditControllerCellEditor.Callback {
List getInput(Object element);
boolean isReadOnly();
}

ComboViewer comboViewer;
ILabelProvider labelProvider;
IStructuredContentProvider contentProvider;
boolean hasFocus;
Object selectedValue;
Object lastValue;

public ComboViewerCellEditor(TableViewerEditController controller, IStructuredContentProvider contentProvider, ILabelProvider labelProvider, Callback callback) {
	super(controller, callback);
	this.contentProvider = contentProvider;
	this.labelProvider = labelProvider;
    create(controller.tableViewer.getTable());
}

@Override
protected Control createEditorControl(Composite parent) {
	int style = getStyle();
	style = style | SWT.DROP_DOWN;
	if (((Callback)callback).isReadOnly())
		style = style | SWT.READ_ONLY;
	Combo combo = new Combo(parent, style);
	combo.setSize(40,10);
	comboViewer = new ComboViewer(combo);
	comboViewer.setContentProvider(contentProvider);
	comboViewer.setLabelProvider(labelProvider);
	combo.addVerifyListener(new VerifyListener() {
		public void verifyText(VerifyEvent evt) {
			if (!hasFocus) return;
			if (!isEditMode()) {
				evt.doit = false;
				return;
			}
			valueChanged(true, true);
		}
	});
	combo.addSelectionListener(new SelectionAdapter() {
        @Override
		public void widgetSelected(SelectionEvent e) {
        	if (!isEditMode()) return;
        	
            selectedValue = readCurrentComboValue();
        }
    });
	combo.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
			hasFocus = false;
		}
        @Override
		public void focusLost(FocusEvent e) {
        	hasFocus = false;
            ComboViewerCellEditor.this.focusLost();
        }
    });
	combo.addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent ev) {
			if (ev.detail == SWT.TRAVERSE_TAB_NEXT ) {
				if (!isEditMode()) {
		            selectedValue = readCurrentComboValue();
					applyEditorValueAndDeactivate();
				}
				controller.processTabNextEvent(ComboViewerCellEditor.this);
			} else if (ev.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
				if (!isEditMode()) {
		            selectedValue = readCurrentComboValue();
					applyEditorValueAndDeactivate();
				}
				controller.processTabPreviousEvent(ComboViewerCellEditor.this);
			}
		}
	});
	combo.addKeyListener(new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.stateMask == 0 && e.character != 0 && e.character != '\t') {
				setEditMode(true);
				return;
			}
			
			if (isEditMode()) return;
			
			if (e.keyCode == SWT.ARROW_UP) {
				applyEditorValueAndDeactivate();
				controller.processCursorUpEvent(ComboViewerCellEditor.this);
			} else if (e.keyCode == SWT.ARROW_DOWN ) {
				applyEditorValueAndDeactivate();
				controller.processCursorDownEvent(ComboViewerCellEditor.this);
			} else if (e.keyCode == SWT.ARROW_RIGHT) {
				e.doit = false;
				applyEditorValueAndDeactivate();
				controller.processTabNextEvent(ComboViewerCellEditor.this);
			} else if (e.keyCode == SWT.ARROW_LEFT ) {
				e.doit = false;
				applyEditorValueAndDeactivate();
				controller.processTabPreviousEvent(ComboViewerCellEditor.this);
			}
		}
	});

	return combo;
}

boolean isLastItemSelected() {
	return comboViewer.getCombo().getSelectionIndex() == comboViewer.getCombo().getItemCount()-1;
}

boolean isFirstItemSelected() {
	return comboViewer.getCombo().getSelectionIndex() == 0;
}

@Override
protected Object doGetValue() {
	return selectedValue;
}

Object readCurrentComboValue() {
	if (((Callback)callback).isReadOnly())
		return ((IStructuredSelection)comboViewer.getSelection()).getFirstElement();
	else
		return comboViewer.getCombo().getText();
}

@Override
protected void doSetFocus() {
	comboViewer.getCombo().setFocus();
}

@Override
protected void doSetEditorValue(Object value) {
	selectedValue = value;
	lastValue = value;
	comboViewer.setInput(((Callback)callback).getInput(controller.getSelectedElement()));
	
    if (((Callback)callback).isReadOnly()) {
		if (value == null)
			comboViewer.setSelection(new StructuredSelection());
		else
			comboViewer.setSelection(new StructuredSelection(value));
    } else {
    	Assert.isTrue(value == null || value instanceof String, "Readonly ComboViewerCellEditors only support Strings");
    	comboViewer.getCombo().setText((String)value);
    }
}

public ILabelProvider getLabelProvider() {
	return labelProvider;
}

public void setLabelProvider(ILabelProvider labelProvider) {
	this.labelProvider = labelProvider;
}

public IStructuredContentProvider getContentProvider() {
	return contentProvider;
}

public void setContentProvider(IStructuredContentProvider contentProvider) {
	this.contentProvider = contentProvider;
}

@Override
protected void focusLost() {
    if (isActivated()) {
        deactivate();
    }
}

void applyEditorValueAndDeactivate() {
	if (!readCurrentComboValue().equals(lastValue)) {
		controller.fireContentModified();
	    fireApplyEditorValue();
	}
    deactivate();
}

}
