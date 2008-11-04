package org.mariella.rcp;

import java.util.Map;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnWidthHandler {

	public static final String RESIZABLE_PROPERTY=TableColumnWidthHandler.class.getSimpleName()+".resizable";

	private boolean updating = false;

public TableColumnWidthHandler() {
}
	
public void handleColumnWidths(final Table table, TableColumn tableCol) {
	tableCol.addControlListener(new ControlAdapter() {
		@Override
		public void controlResized(ControlEvent e) {
			handleResize(table);
		}
	});
}

protected void handleResize(Table table) {
	if (updating) return;
	updating = true;
	try {
		if (table.isDisposed()) return;
		TableColumn[] tc = table.getColumns();
		if (tc.length == 0) return;
		int currentAvailableWidth = 0;
		int nonAvailableWidth = 0;
		for (int i = 0; i < tc.length; i++) {
			if (shouldResize(tc[i]))
				currentAvailableWidth += tc[i].getWidth();
			else
				nonAvailableWidth += tc[i].getWidth();
		}
		
		int newAvailableWidth = table.getBounds().width
				- (table.getBorderWidth() * 2) - nonAvailableWidth;
		float factor = (float) newAvailableWidth / (float) currentAvailableWidth;
	
		int remainingAvailableWidth = newAvailableWidth;
		for (int i = 0; i < tc.length - 1; i++) {
			if (shouldResize(tc[i])) {
				tc[i].setWidth((int) ((float) tc[i].getWidth() * factor));
				remainingAvailableWidth -= tc[i].getWidth();
			}
		}
		tc[tc.length - 1].setWidth(remainingAvailableWidth);
		table.layout(true, true);
	} finally {
		updating = false;
	}
}

@SuppressWarnings("unchecked")
private boolean shouldResize(TableColumn tableColumn) {
	Map<String,Object> props = (Map<String,Object>)tableColumn.getData();
	if (props == null) return false;
	Boolean resizable = (Boolean)props.get(RESIZABLE_PROPERTY);
	if (resizable == null) return false;
	return (Boolean)resizable;
}
	
public void handleColumnWidths(final Table table) {
	table.addControlListener(new ControlAdapter() {
		
		@Override
		public void controlResized(ControlEvent e) {
			handleResize(table);
		}
	});

}

}
