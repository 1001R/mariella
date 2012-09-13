package org.mariella.cat.ui.controls.decorations;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.mariella.cat.core.validation.IMessageChangedListener;
import org.mariella.cat.core.validation.Message;
import org.mariella.cat.core.validation.Messages;


public final class ControlDecorationUtil {

	private ControlDecorationUtil() {
		// hide
	}

	public static void messageDecorate(final Control controlToDecorate, final Messages messages, final Object... messageKeys) {
		if (controlToDecorate != null && !controlToDecorate.isDisposed()) {
			final IMessageChangedListener messageListener = new IMessageChangedListener() {
				private ControlDecorator.Icons lastIcon = null;
				@Override
				public void messagesChanged(Messages messages) {
					if (controlToDecorate != null && !controlToDecorate.isDisposed()) {
						Message.Type mostSevereType = messages.getMostSevereMessageType(messageKeys);
						ControlDecorator.Icons icon = null;
						if (mostSevereType == Message.Type.ERROR) {
							icon = ControlDecorator.Icons.error;
						} else if (mostSevereType == Message.Type.WARNING) {
							icon = ControlDecorator.Icons.warning;
						}
						if (icon != lastIcon) {
							if (lastIcon != null) {
								ControlDecorator.removeDecoration(controlToDecorate, lastIcon);
							}
							if (icon != null) {
								ControlDecorator.decorate(controlToDecorate, icon);
							}
						}
					}
				}
			};
			messageListener.messagesChanged(messages);
			messages.addMessageChangedListener(messageListener);
			controlToDecorate.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					messages.removeMessageChangedListener(messageListener);
				}
			});		
			
		}
	}

}
