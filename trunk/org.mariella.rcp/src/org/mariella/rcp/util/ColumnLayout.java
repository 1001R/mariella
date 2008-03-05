package org.mariella.rcp.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

public class ColumnLayout extends Layout {

public int margin = 4;
public int spacing = 2;

Point[] sizes;

int maxWidth, totalHeight;

public ColumnLayout() {
	
}

public ColumnLayout(int margin, int spacing) {
	this.margin = margin;
	this.spacing = spacing;
}

protected Point computeSize(Composite composite, int wHint, int hHint,
		boolean flushCache) {
	Control children[] = composite.getChildren();
	if (flushCache || sizes == null || sizes.length != children.length) {
		initialize(children);
	}

	int width = wHint, height = hHint;
	if (wHint == SWT.DEFAULT)
		width = maxWidth;

	if (hHint == SWT.DEFAULT)
		height = totalHeight;
	return new Point(width + 2 * margin, height + 2 * margin);
}

protected void layout(Composite composite, boolean flushCache) {
	Control children[] = composite.getChildren();
	if (flushCache || sizes == null || sizes.length != children.length) {
		initialize(children);
	}

	Rectangle rect = composite.getClientArea();
	int x = margin, y = margin;
	int width = Math.max(rect.width - 2 * margin, maxWidth);
	for (int i = 0; i < children.length; i++) {
		int height = sizes[i].y;
		children[i].setBounds(x, y, width, height);
		y += height + spacing;
	}
}

void initialize(Control children[]) {
	maxWidth = 0;
	totalHeight = 0;
	sizes = new Point[children.length];
	for (int i = 0; i < children.length; i++) {
		sizes[i] = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		maxWidth = Math.max(maxWidth, sizes[i].x);
		totalHeight += sizes[i].y;
	}
	totalHeight += (children.length - 1) * spacing;
}

}