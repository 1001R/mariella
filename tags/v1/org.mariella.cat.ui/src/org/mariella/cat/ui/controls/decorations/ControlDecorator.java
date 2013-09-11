package org.mariella.cat.ui.controls.decorations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.mariella.cat.ui.CatUi;

public final class ControlDecorator {

	public enum Icons {
		error(1000),
		warning(2000),
		contentAssistant(3000);

		private int priority;

		private Icons(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}
	}

	private class DecoratorListener implements Listener {
		private boolean imageVisible = false;
		private Rectangle getImageBounds(Control control) {
			Image image = getCurrentImage();
			Point size = control.getSize();
			int borderWidth = control.getBorderWidth();
			Rectangle imageBounds = image.getBounds();
			imageBounds.x = size.x-borderWidth-imageBounds.width-2;
			imageBounds.y = borderWidth+1;
			return imageBounds;
		}
		@Override
		public void handleEvent(Event event) {
			if (getCurrentImage() != null) {
				if (event.type == SWT.Paint) {
					Rectangle imageBounds = getImageBounds(control);
					event.gc.drawImage(getCurrentImage(), imageBounds.x, imageBounds.y);
				} else if (event.type == SWT.KeyUp || event.type == SWT.MouseDown) {
					Rectangle imageBounds = getImageBounds(control);
					control.redraw(imageBounds.x, imageBounds.y, imageBounds.width, imageBounds.height, false);
				}
				imageVisible = true;
			} else if (imageVisible) {
				control.redraw();
				imageVisible = false;
			}
		}
	};

	private static class DecorationImage implements Comparable<DecorationImage> {
		private Image image;
		private int priority;

		private DecorationImage(Image image, int priority) {
			this.image = image;
			this.priority = priority;
		}

		@Override
		public int compareTo(DecorationImage o) {
			return cmp(this.priority, o.priority);
		}
		
		private int cmp(int v1, int v2) {
			return v1 < v2 ? -1 : (v1 == v2 ? 0 : 1);
		}
	}

	private Control control;
	private List<DecorationImage> images = new ArrayList<ControlDecorator.DecorationImage>(3);
	private Listener listener = new DecoratorListener();

	private ControlDecorator(Control control, Image image, int priority) {
		this.control = control;
		this.images.add(new DecorationImage(image, priority));
		control.setData(ControlDecorator.class.getName(), this);
		control.addListener(SWT.Paint, listener);
		control.addListener(SWT.KeyUp, listener);
		control.addListener(SWT.MouseDown, listener);
		control.redraw();
	}

	private void setImage(Image image, int priority) {
		Iterator<DecorationImage> it = images.iterator();
		while (it.hasNext()) {
			DecorationImage img = it.next();
			if (img.priority == priority) {
				if (image != null) {
					img.image = image;
				} else {
					it.remove();
					if (images.isEmpty()) {
						unhook();
					}
				}
				control.redraw();
				return;
			}
		}
		if (image != null) {
			images.add(new DecorationImage(image, priority));
			Collections.sort(images);
			control.redraw();
		}
	}

	private Image getCurrentImage() {
		if (images.isEmpty()) {
			return null;
		} else {
			return images.get(0).image;
		}
	}

	private void unhook() {
		control.setData(ControlDecorator.class.getName(), null);
		control.removeListener(SWT.Paint, listener);
		control.removeListener(SWT.Selection, listener);
	}

	public static void decorate(Control control, Icons image) {
		Image img = null;
		if (image == Icons.error) {
			img = CatUi.getImageDescriptor("icons/error.png").createImage();
		} else if (image == Icons.warning) {
			img = CatUi.getImageDescriptor("icons/alt_window_16.gif").createImage();
		} else if (image == Icons.contentAssistant) {
			img = CatUi.getImageDescriptor("icons/alt_window_16.gif").createImage();
		}
		decorate(control, img, image.getPriority());
	}

	private static void decorate(Control control, Image image, int priority) {
		ControlDecorator decorator = (ControlDecorator) control.getData(ControlDecorator.class.getName());
		if (decorator == null && image != null) {
			decorator = new ControlDecorator(control, image, priority);
		} else if (decorator != null) {
			decorator.setImage(image, priority);
		}
	}

	public static void removeDecoration(Control control, Icons image) {
		decorate(control, (Image) null, image.getPriority());
	}

}
