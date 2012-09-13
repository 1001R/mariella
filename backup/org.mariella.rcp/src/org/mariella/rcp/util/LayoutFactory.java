package org.mariella.rcp.util;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class LayoutFactory {

public static GridLayout createGridLayout(int cols) {
	GridLayout gridLayout = new GridLayout(cols, false);
	gridLayout.verticalSpacing = 2;
	gridLayout.horizontalSpacing = 2;
	gridLayout.marginLeft = 0;
	gridLayout.marginTop = 0;
	gridLayout.marginRight = 0;
	return gridLayout;
}

public static GridData createHorizontalFillingGridData() {
	GridData gridData = new GridData();
	gridData.grabExcessHorizontalSpace = true;
	gridData.widthHint = GridData.FILL_HORIZONTAL;
	return gridData;
}

}
