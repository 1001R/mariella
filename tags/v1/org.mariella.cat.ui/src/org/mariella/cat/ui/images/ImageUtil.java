package org.mariella.cat.ui.images;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public final class ImageUtil {
	
	private static Image cbImgChecked = null;
	private static Image cbImgUnchecked = null;
	private static Image cbImgCheckedDisabled = null;
	private static Image cbImgUncheckedDisabled = null;
	
	private ImageUtil() {
	}

	public static Image resize(Display display, Image sourceImage, int width, int height) {
		Image scaledImage = new Image(display, width, height);
		GC gc = new GC(scaledImage);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(sourceImage, 0, 0, sourceImage.getBounds().width, sourceImage.getBounds().height, 0, 0, width, height);
		gc.dispose();
		return scaledImage;
	}	
	
	public static Image createBlankImage(Display display, Color color, int width, int height) {
		Image image = new Image(display, width, height);
		GC gc = new GC(image);
		gc.setForeground(color);
		gc.fillRectangle(0, 0, width, height);
		gc.dispose();
		return image;
	}
	
	public static Image combine(Image baseImage, Image decoratorImage, int horAlignment, int vertAlignment, int dx, int dy) {
		Rectangle baseBounds = baseImage.getBounds();
		Image image = new Image(baseImage.getDevice(), baseBounds.width, baseBounds.height);
		GC gc = new GC(image);
		gc.drawImage(baseImage, 0, 0);
		Rectangle decoratorBounds = decoratorImage.getBounds();
		gc.drawImage(decoratorImage, align(horAlignment, 0, baseBounds.width, decoratorBounds.width)+dx, align(vertAlignment, 0, baseBounds.height, decoratorBounds.height)+dy);
		gc.dispose();
		return image;
	}
	
	public static Image surround(Image image, Color color, int left, int top, int right, int bottom) {
		Rectangle bounds = image.getBounds();
		Image newImage = new Image(image.getDevice(), bounds.width+left+right, bounds.height+top+bottom);
		GC gc = new GC(newImage);
		gc.setForeground(color);
		gc.drawRectangle(0, 0, bounds.width+left+right, bounds.height+top+bottom);
		gc.drawImage(image, left, top);
		gc.dispose();
		return image;
	}

	public static int align(int alignment, int from, int to, int size) {
		if ((alignment & SWT.CENTER) != 0) {
			return from+Math.max(0, to-from-size)/2;
		} else if ((alignment & SWT.RIGHT) != 0) {
			return to-size;
		} else {
			return from;
		}
	}
	
	private static void ensureCheckboxImages() {
		if (cbImgChecked == null) {
			Shell shell = new Shell(SWT.NO_TRIM);
			Button cb = new Button(shell, SWT.CHECK);
			cb.setLocation(0, 0);
			cb.setSize(cb.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			cb.setBackground(cb.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
			shell.pack();
			shell.open();
			cb.setSelection(true);
			cbImgChecked = createImage(cb);
			cb.setSelection(false);
			cbImgUnchecked = createImage(cb);
			cb.setEnabled(false);
			cb.setSelection(true);
			cbImgCheckedDisabled = createImage(cb);
			cb.setSelection(false);
			cbImgUncheckedDisabled = createImage(cb);
			shell.close();
		}
	}
	
	private static Image createImage(Control control) {
		Point size = control.getSize();
		Image image = new Image(control.getDisplay(), size.x, size.y);
		GC gc = new GC(image);
		control.print(gc);
		gc.dispose();
		return image;
	}
	
	public static Image getCbImgChecked() {
		ensureCheckboxImages();
		return cbImgChecked;
	}
	
	public static Image getCbImgCheckedDisabled() {
		ensureCheckboxImages();
		return cbImgCheckedDisabled;
	}
	
	public static Image getCbImgUnchecked() {
		ensureCheckboxImages();
		return cbImgUnchecked;
	}
	
	public static Image getCbImgUncheckedDisabled() {
		ensureCheckboxImages();
		return cbImgUncheckedDisabled;
	}
	
	
}
