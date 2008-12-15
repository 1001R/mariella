package org.mariella.rcp.databinding.internal;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class TableViewerToolTipHandler {

Listener labelListener = new Listener() {
	public void handleEvent(Event event) {
		Label label = (Label) event.widget;
		Shell shell = label.getShell();
		switch (event.type) {
		case SWT.MouseExit:
			shell.dispose();
			break;
		}
	}
};

Listener tableListener = new Listener() {
	Shell tip = null;

	Label label = null;

	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.Dispose:
		case SWT.KeyDown:
		case SWT.MouseMove: {
			if (tip == null)
				break;
			tip.dispose();
			tip = null;
			label = null;
			break;
		}
		case SWT.MouseHover: {
			
			TableItem item = table.getItem(new Point(event.x, event.y));
			if (item != null) {
				int newColumnIndex = -1;
				int columnCount = table.getColumnCount();
				if (columnCount > 0) {
					for (int i = 0; i < columnCount; i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(event.x, event.y)) {
							newColumnIndex = i;
							break;
						}
					}
				}
				
				Object element = item.getData();
				if (newColumnIndex != -1 && item != null) {
					String toolTipText = tableController.getColumnToolTip(element, newColumnIndex);
					if (toolTipText == null) return;
					if (tip != null && !tip.isDisposed())
						tip.dispose();
					tip = new Shell(table.getShell(), SWT.ON_TOP | SWT.TOOL);
					tip.setLayout(new FillLayout());
					label = new Label(tip, SWT.NONE);
					label.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
					label.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
					label.setData("_TABLEITEM", item);
					label.setText(toolTipText);
					label.addListener(SWT.MouseExit, labelListener);
					label.addListener(SWT.MouseDown, labelListener);
					Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
					Point pt = table.toDisplay(event.x, event.y);
					tip.setBounds(pt.x, pt.y, size.x, size.y);
					tip.setVisible(true);
				}
			}
		}
		}
	}
};

TableViewer tableViewer;
TableController tableController;
Table table;

TableViewerToolTipHandler(TableController tableController, TableViewer tableViewer) {
	this.tableController = tableController;
	this.tableViewer = tableViewer;
	this.table = tableViewer.getTable();
	table.addListener(SWT.Dispose, tableListener);
    table.addListener(SWT.KeyDown, tableListener);
    table.addListener(SWT.MouseMove, tableListener);
    table.addListener(SWT.MouseHover, tableListener);
}

}
