package org.mariella.rcp.table;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DefaultTableLabelProvider implements ITableLabelProvider, ILabelProvider, ITableFontProvider {

TableDescriptor tableDescriptor;

public DefaultTableLabelProvider(TableDescriptor tableDescriptor) {
	this.tableDescriptor = tableDescriptor;
}

public Image getColumnImage(Object element, int index) {
	TableColumnDescriptor colDescr = tableDescriptor.getColumnDescriptors()[index];
	TableColumnIconProvider prov = colDescr.iconProvider;
	if (prov != null)
		return prov.getIcon(element);

	return null;
}

public String getColumnText(Object element, int index) {
	TableColumnDescriptor colDescr = tableDescriptor.getColumnDescriptors()[index];
	if (!(colDescr instanceof TablePropertyColumnDescriptor))
		return "";
	TablePropertyColumnDescriptor propColDescr = (TablePropertyColumnDescriptor)colDescr;
	Object value = propColDescr.readValue(element);
	if (value == null) return "";
	if (propColDescr.getFormat() == null) return value.toString();
	return propColDescr.getFormat().format(value);
}

public void addListener(ILabelProviderListener arg0) {}

public void dispose() {}

public boolean isLabelProperty(Object arg0, String arg1) { return false;}

public void removeListener(ILabelProviderListener arg0) {}

public Image getImage(Object element) {
	return getColumnImage(element, 0);
}

public String getText(Object element) {
	return getColumnText(element, 0);
}

public Font getFont(Object element, int index) {
	TableColumnDescriptor colDescr = tableDescriptor.getColumnDescriptors()[index];
	if (!(colDescr instanceof TablePropertyColumnDescriptor))
		return null;
	TablePropertyColumnDescriptor propColDescr = (TablePropertyColumnDescriptor)colDescr;	
	if (!propColDescr.canWrite(element)) {
		Font defaultFont = Display.getCurrent().getSystemFont();
		FontData fontData = defaultFont.getFontData()[0];
		fontData.setStyle(SWT.ITALIC);
		return new Font(Display.getCurrent(), new FontData[]{fontData});
	}
	return null;

}

}
