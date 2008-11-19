package org.mariella.rcp.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

public class EditorTableSelectionRenderSupport {

public final static void installSoftSelectionStyle(final Table table) {
	final Color selectionColor1 = new Color(Display.getCurrent(), 255, 255, 80);
	final Color selectionColor2 = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	// do not show selection bar, our CellEditors will animate selected cells
	table.addListener(SWT.EraseItem, new Listener() {
		public void handleEvent(Event event) {
			if ((event.detail & SWT.SELECTED) == 0) return; // item not selected
			int clientWidth = table.getClientArea().width;
			GC gc = event.gc;
			Color oldBackground = gc.getBackground();
			gc.setForeground(selectionColor1);
			gc.setBackground(selectionColor2);
			gc.fillGradientRectangle(0, event.y, clientWidth, event.height, false);
			gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			gc.setBackground(oldBackground);
			event.detail &= ~SWT.SELECTED;		
		}
	});
}

}
