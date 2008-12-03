package org.mariella.rcp.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class RowLayout extends Layout {

public int margin = 4;
public int spacing = 2;

Point[] sizes;

int maxHeight, totalWidth;

Map<Control, Boolean> visibleMap = new HashMap<Control, Boolean>();

public RowLayout() {
	
}

public RowLayout(int margin, int spacing) {
	this.margin = margin;
	this.spacing = spacing;
}

public void setVisible(Control control, boolean visible) {
	visibleMap.put(control, visible);
}

@Override
protected Point computeSize(Composite composite, int wHint, int hHint,
		boolean flushCache) {
	Control children[] = composite.getChildren();
	if (flushCache || sizes == null || sizes.length != children.length) {
		initialize(children);
	}

	int height = hHint, width = wHint;
	if (hHint == SWT.DEFAULT)
		height = maxHeight;

	if (wHint == SWT.DEFAULT)
		width = totalWidth;
	return new Point(width + 2 * margin, height + 2 * margin);
}

@Override
protected void layout(Composite composite, boolean flushCache) {
	Control children[] = composite.getChildren();
	if (flushCache || sizes == null || sizes.length != children.length) {
		initialize(children);
	}

	Rectangle rect = composite.getClientArea();
	int x = margin, y = margin;
	int height = Math.max(rect.height - 2 * margin, maxHeight);
	
	for (int i = 0; i < children.length; i++) {
		Button b = (Button)children[i];
		
		if (sizes[i] == null) {
			children[i].setSize(new Point(0,0));
			continue;
		}
		int width = sizes[i].x;
		children[i].setBounds(x, y, width, height);
		x += width+ spacing;
	}
}

void initialize(Control children[]) {
	maxHeight = 0;
	totalWidth = 0;
	sizes = new Point[children.length];
	for (int i = 0; i < children.length; i++) {
		if (!isVisible(children[i])) {
			sizes[i] = null;
			continue;
		}
			
		sizes[i] = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		maxHeight = Math.max(maxHeight, sizes[i].y);
		totalWidth += sizes[i].x;
	}
	totalWidth += (children.length - 1) * spacing;
}

public boolean isVisible(Control control) {
	Boolean b = visibleMap.get(control);
	if (b == null) return true;
	return b;
}

}