package org.mariella.rcp.forms;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class FontToolkit {
	
	private static LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());


public Font deriveFont(Font font, int heightDelta, int styleDeltas) {
	return deriveFont(font, null, heightDelta, styleDeltas);
}

public Font deriveFont(Font font, String fontName, int heightDelta, int styleDeltas) {
	FontData[] fontData = FontDescriptor.copy(font.getFontData());
	
	for (int i=0; i<fontData.length; i++) {
		if (fontName != null)
			fontData[i].setName(fontName);
		fontData[i].setHeight(font.getFontData()[i].getHeight()+heightDelta);
		fontData[i].setStyle(font.getFontData()[i].getStyle() | styleDeltas);
	}
	FontDescriptor fontDescriptor = FontDescriptor.createFrom(fontData);
	return (Font)resourceManager.get(fontDescriptor);
}

}
