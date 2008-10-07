package org.mariella.rcp;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnWidthHandler {

	public void handleColumnWidths(final Table table) {
		table.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (table.isDisposed()) return;
				TableColumn[] tc = table.getColumns();
				int currentWidth = 0;
				for (int i = 0; i < tc.length; i++) {
					currentWidth += tc[i].getWidth();
				}
				
				int availableWidth = table.getBounds().width
						- (table.getBorderWidth() * 2);
				float factor = (float) availableWidth / (float) currentWidth;

				int remainingWidth = availableWidth;
				for (int i = 0; i < tc.length - 1; i++) {
					tc[i].setWidth((int) ((float) tc[i].getWidth() * factor));
					remainingWidth -= tc[i].getWidth();
				}
				tc[tc.length - 1].setWidth(remainingWidth);
				table.layout(true, true);
			}
		});

	}

}
