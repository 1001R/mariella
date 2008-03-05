package org.mariella.rcp.table.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public abstract class TableViewerEditControllerCellEditor extends CellEditor {


/**
 * Special Layout Impl.
 * Behaves like the StackLayout, but instead of setting the controls to visible=false,
 * it sets the size of the "invisble" controls to 0/0.
 * This allows the invisible controls to process input.
 * 
 * @author martin
 *
 */
public class StackLayoutImpl extends Layout {

/**
 * topControl the Control that is displayed at the top of the stack.
 * All other controls that are children of the parent composite will not be visible.
 */

	/**
 * marginWidth specifies the number of pixels of horizontal margin
 * that will be placed along the left and right edges of the layout.
 *
 * The default value is 0.
 */
	public int marginWidth = 0;
/**
 * marginHeight specifies the number of pixels of vertical margin
 * that will be placed along the top and bottom edges of the layout.
 *
 * The default value is 0.
 */
public int marginHeight = 0;

	/**
	 * topControl the Control that is displayed at the top of the stack.
	 * All other controls that are children of the parent composite will not be visible.
	 */
	public Control topControl;

protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
Control children[] = composite.getChildren();
int maxWidth = 0;
int maxHeight = 0;
for (int i = 0; i < children.length; i++) {
	Point size = children[i].computeSize(wHint, hHint, flushCache);
	maxWidth = Math.max(size.x, maxWidth);
	maxHeight = Math.max(size.y, maxHeight);
}
int width = maxWidth + 2 * marginWidth;
int height = maxHeight + 2 * marginHeight;
if (wHint != SWT.DEFAULT) width = wHint;
if (hHint != SWT.DEFAULT) height = hHint;
return new Point(width, height);
}

protected boolean flushCache(Control control) {
return true;
}

protected void layout(Composite composite, boolean flushCache) {
Control children[] = composite.getChildren();
Rectangle rect = composite.getClientArea();
rect.x += marginWidth;
rect.y += marginHeight;
rect.width -= 2 * marginWidth;
rect.height -= 2 * marginHeight;
for (int i = 0; i < children.length; i++) {
	if (children[i] == topControl)
		children[i].setBounds(rect);
	else
		children[i].setBounds(0,0,0,0);
		
}
}

String getName () {
String string = getClass ().getName ();
int index = string.lastIndexOf ('.');
if (index == -1) return string;
return string.substring (index + 1, string.length ());
}

/**
* Returns a string containing a concise, human-readable
* description of the receiver.
*
* @return a string representation of the layout
*/
public String toString () {
	String string = getName ()+" {";
	if (marginWidth != 0) string += "marginWidth="+marginWidth+" ";
	if (marginHeight != 0) string += "marginHeight="+marginHeight+" ";
	if (topControl != null) string += "topControl="+topControl+" ";
	string = string.trim();
	string += "}";
	return string;
}
}



public interface Callback {
String valueToText(Object value);
}

TableViewerEditController controller;

Callback callback;
Composite stackComposite;
StackLayoutImpl stackLayout;
Label disabledStateLabel;
Control editorControl;
Color defaultColor;

public TableViewerEditControllerCellEditor(TableViewerEditController controller, Callback callback) {
	this.controller = controller;
	this.callback = callback;
}

protected final Control createControl(Composite parent) {
	stackComposite = new Composite(parent, SWT.NONE);
	stackLayout = new StackLayoutImpl();
	stackLayout.marginHeight=1;
	stackLayout.marginWidth =1;
	stackComposite.setLayout(stackLayout);
	
	disabledStateLabel = new Label(stackComposite, SWT.NONE);
	disabledStateLabel.addMouseListener(new MouseAdapter() {
		public void mouseDoubleClick(MouseEvent e) {
			setEditMode(true);
		}
	});
	
	stackLayout.topControl = disabledStateLabel;
	disabledStateLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	
	editorControl = createEditorControl(stackComposite);
	
	editorControl.addMouseListener(new MouseAdapter() {
		public void mouseDoubleClick(MouseEvent e) {
			setEditMode(true);
		}
	});
	editorControl.addKeyListener(new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.keyCode == SWT.F2)
				setEditMode(true);
		}
	});
	
	return stackComposite;
}

protected final void doSetValue(Object value) {
	disabledStateLabel.setText(callback.valueToText(value));
	doSetEditorValue(value);
}

protected abstract void doSetEditorValue(Object value);

protected void setEditMode(boolean editMode) {
	stackLayout.topControl = editMode ? editorControl : disabledStateLabel;
	stackComposite.setBackground(Display.getCurrent().getSystemColor(editMode ? SWT.COLOR_GRAY: SWT.COLOR_DARK_GRAY));
	stackComposite.layout();
}

protected boolean isEditMode() {
	return stackLayout.topControl == editorControl;
}

protected abstract Control createEditorControl(Composite parent);

@Override
public final void activate() {
	controller.activated(this);
	super.activate();
}

public final void deactivate() {
	super.deactivate();
	controller.deactivated(this);
	setEditMode(false);
}

}
