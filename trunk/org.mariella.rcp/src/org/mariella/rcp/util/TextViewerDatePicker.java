package org.mariella.rcp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TypedListener;

import com.tiff.common.ui.datepicker.DatePicker;


public final class TextViewerDatePicker extends Composite {

public static interface Callback {
TextViewer createDateTextViewer(Composite parent);
DateFormat getDateFormat();
}




private Button arrow;
private DatePicker dp;
private boolean hasFocus;

private boolean isClosePopupWithSingleMouseClick = false;
private Shell popup;
private TextViewer textViewer;

private DateFormat format = null;
private Callback callback;


public TextViewerDatePicker(Composite parent, int style, Callback callback) {
    super(parent, checkStyle(style));
    this.callback = callback;
    this.format = callback.getDateFormat();
    style = getStyle();

    int textStyle = SWT.SINGLE;

    if ((style & SWT.READ_ONLY) != 0) {
        textStyle |= SWT.READ_ONLY;
    }

    if ((style & SWT.FLAT) != 0) {
        textStyle |= SWT.FLAT;
    }

    textViewer = callback.createDateTextViewer(this);

    popup = new Shell(getShell(), SWT.NO_TRIM);

    int pickerStyle = SWT.SINGLE;

    if ((style & SWT.FLAT) != 0) {
        pickerStyle |= SWT.FLAT;
    }

    dp = new DatePicker(popup, pickerStyle);

    int arrowStyle = SWT.ARROW | SWT.DOWN;

    if ((style & SWT.FLAT) != 0) {
        arrowStyle |= SWT.FLAT;
    }

    arrow = new Button(this, arrowStyle);

    Listener listener = new Listener() {
            public void handleEvent(Event event) {
                if (popup == event.widget) {
                    popupEvent(event);

                    return;
                }

                if (textViewer.getTextWidget() == event.widget) {
                    textEvent(event);

                    return;
                }

                if (dp == event.widget) {
                    dpEvent(event);

                    return;
                }

                if (arrow == event.widget) {
                    arrowEvent(event);

                    return;
                }

                if (TextViewerDatePicker.this == event.widget) {
                    comboEvent(event);

                    return;
                }
            }
        };

    int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };

    for (int i = 0; i < comboEvents.length; i++)
        this.addListener(comboEvents[i], listener);

    int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Deactivate };

    for (int i = 0; i < popupEvents.length; i++)
        popup.addListener(popupEvents[i], listener);

    int[] textEvents = {
        SWT.KeyDown, SWT.KeyUp, SWT.Modify, SWT.MouseDown, SWT.MouseUp,
        SWT.Traverse, SWT.FocusIn, SWT.FocusOut
    };

    for (int i = 0; i < textEvents.length; i++)
    	textViewer.getTextWidget().addListener(textEvents[i], listener);

    int[] dpEvents = {
        SWT.MouseUp, SWT.MouseDoubleClick, SWT.Selection, SWT.Traverse,
        SWT.KeyDown, SWT.KeyUp, SWT.FocusIn, SWT.FocusOut
    };

    for (int i = 0; i < dpEvents.length; i++)
        dp.addListener(dpEvents[i], listener);

    int[] arrowEvents = { SWT.Selection, SWT.FocusIn, SWT.FocusOut };

    for (int i = 0; i < arrowEvents.length; i++)
        arrow.addListener(arrowEvents[i], listener);

    initAccessible();
}

public static int checkStyle(int style) {
    int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT;

    return style & mask;
}

public void addModifyListener(ModifyListener listener) {
    checkWidget();

    if (listener == null) {
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }

    TypedListener typedListener = new TypedListener(listener);
    addListener(SWT.Modify, typedListener);
}

public void addSelectionListener(SelectionListener listener) {
    checkWidget();

    if (listener == null) {
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }

    TypedListener typedListener = new TypedListener(listener);
    addListener(SWT.Selection, typedListener);
    addListener(SWT.DefaultSelection, typedListener);
}

private void arrowEvent(Event event) {
    switch (event.type) {
    case SWT.FocusIn: {
        if (hasFocus) {
            return;
        }

        hasFocus = true;

        if (getEditable()) {
            //textViewer.getTextWidget().selectAll();
        }

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.FocusIn, e);

        break;
    }

    case SWT.FocusOut: {
        Control focusControl = getDisplay().getFocusControl();

        if ((focusControl == dp) || (focusControl == textViewer.getTextWidget())) {
            return;
        }

        hasFocus = false;

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.FocusOut, e);

        break;
    }

    case SWT.Selection: {
        dropDown(!isDropped());

        break;
    }
    }
}

public void clearSelection() {
    checkWidget();
    textViewer.getTextWidget().setSelection(0,0);
    dp.reset();
}

private void comboEvent(Event event) {
    switch (event.type) {
    case SWT.Dispose:

        if ((popup != null) && !popup.isDisposed()) {
            popup.dispose();
        }

        popup = null;
        textViewer = null;
        dp = null;
        arrow = null;

        break;

    case SWT.Move:
        dropDown(false);

        break;

    case SWT.Resize:
        internalLayout();

        break;
    }
}

@Override
public Point computeSize(int wHint, int hHint, boolean changed) {
    checkWidget();

    int width = 0;
    int height = 0;
    Point textSize = textViewer.getTextWidget().computeSize(wHint, SWT.DEFAULT, changed);
    Point arrowSize = arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
    Point listSize = dp.computeSize(wHint, SWT.DEFAULT, changed);
    int borderWidth = getBorderWidth();

    height = Math.max(hHint,
            Math.max(textSize.y, arrowSize.y) + (2 * borderWidth));
    width = Math.max(wHint,
            Math.max(textSize.x + arrowSize.x + (2 * borderWidth),
                listSize.x + 2));

    return new Point(width, height);
}

private void dpEvent(Event event) {
    switch (event.type) {
    case SWT.FocusIn: {
        if (hasFocus) {
            return;
        }

        hasFocus = true;

        if (getEditable()) {
            //textViewer.getTextWidget().selectAll();
        }

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.FocusIn, e);

        break;
    }

    case SWT.FocusOut: {
        Control focusControl = getDisplay().getFocusControl();

        if ((focusControl == textViewer.getTextWidget()) || (focusControl == arrow)) {
            return;
        }

        hasFocus = false;

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.FocusOut, e);

        break;
    }

    case SWT.MouseDown: {
        if (event.button != 1) {
            return;
        }

        dropDown(false);

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.DefaultSelection, e);

        break;
    }

    case SWT.Selection: {
        // sebthom
        if (!isClosePopupWithSingleMouseClick) {
            Date date = dp.getDate();
            textViewer.getDocument().set(getFormat().format(date));
            //textViewer.getTextWidget().selectAll();

            Event e = new Event();
            e.time = event.time;
            e.stateMask = event.stateMask;
            e.doit = event.doit;
            notifyListeners(SWT.Selection, e);
            event.doit = e.doit;

            break;
        }

        // otherwise perform the code of SWT.MouseDoubleClick
    }

    case SWT.MouseDoubleClick: {
        dropDown(false);

        Date date = dp.getDate();

        // sebthom
        if (date == null) {
            textViewer.getDocument().set("");
        } else {
            textViewer.getDocument().set(getFormat().format(date));
            //textViewer.getTextWidget().selectAll();
        }

        Event e = new Event();
        e.time = event.time;
        e.stateMask = event.stateMask;
        e.doit = event.doit;
        notifyListeners(SWT.Selection, e);
        event.doit = e.doit;

        break;
    }

    case SWT.Traverse: {
        switch (event.detail) {
        case SWT.TRAVERSE_TAB_NEXT:
        case SWT.TRAVERSE_RETURN:
        case SWT.TRAVERSE_ESCAPE:
        case SWT.TRAVERSE_ARROW_PREVIOUS:
        case SWT.TRAVERSE_ARROW_NEXT:
            event.doit = false;

            break;
        }

        Event e = new Event();
        e.time = event.time;
        e.detail = event.detail;
        e.doit = event.doit;
        e.keyCode = event.keyCode;
        notifyListeners(SWT.Traverse, e);
        event.doit = e.doit;

        break;
    }

    case SWT.KeyUp: {
        Event e = new Event();
        e.time = event.time;
        e.character = event.character;
        e.keyCode = event.keyCode;
        e.stateMask = event.stateMask;
        notifyListeners(SWT.KeyUp, e);

        break;
    }

    case SWT.KeyDown: {
        if (event.character == SWT.ESC) {
            // escape key cancels popup dp
            dropDown(false);
        }

        if ((event.character == SWT.CR) || (event.character == '\t')) {
            // Enter and Tab cause default selection
            dropDown(false);

            Event e = new Event();
            e.time = event.time;
            e.stateMask = event.stateMask;
            notifyListeners(SWT.DefaultSelection, e);
        }

        //At this point the widget may have been disposed.
        // If so, do not continue.
        if (isDisposed()) {
            break;
        }

        Event e = new Event();
        e.time = event.time;
        e.character = event.character;
        e.keyCode = event.keyCode;
        e.stateMask = event.stateMask;
        notifyListeners(SWT.KeyDown, e);

        break;
    }
    }
}

private void dropDown(boolean drop) {
	if (!textViewer.getTextWidget().isEnabled()) return;
    if (drop == isDropped()) {
        return;
    }

    if (!drop) {
        popup.setVisible(false);
        textViewer.getTextWidget().setFocus();

        return;
    }

    Rectangle listRect = dp.getBounds();
    Point point = getParent().toDisplay(getLocation());
    Point comboSize = getSize();
    int width = Math.max(comboSize.x, listRect.width + 2);
    popup.setBounds(point.x, point.y + comboSize.y, width,
        listRect.height + 2);
    popup.setVisible(true);
    dp.setFocus();
}

@Override
public Control[] getChildren() {
    checkWidget();
    return new Control[0];
}

public Date getDate() {
    checkWidget();
    return dp.getDate();
}

public boolean getEditable() {
    return textViewer.getTextWidget().getEditable();
}

public String getText() {
    return textViewer.getTextWidget().getText();
}

public int getTextHeight() {
    checkWidget();
    return textViewer.getTextWidget().getLineHeight();
}

private void initAccessible() {
    getAccessible().addAccessibleListener(new AccessibleAdapter() {
            @Override
			public void getHelp(AccessibleEvent e) {
                e.result = getToolTipText();
            }
        });

    getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
            @Override
			public void getChildAtPoint(AccessibleControlEvent e) {
                Point testPoint = toControl(new Point(e.x, e.y));

                if (getBounds().contains(testPoint)) {
                    e.childID = ACC.CHILDID_SELF;
                }
            }

            @Override
			public void getChildCount(AccessibleControlEvent e) {
                e.detail = 0;
            }

            @Override
			public void getLocation(AccessibleControlEvent e) {
                Rectangle location = getBounds();
                Point pt = toDisplay(new Point(location.x, location.y));
                e.x = pt.x;
                e.y = pt.y;
                e.width = location.width;
                e.height = location.height;
            }

            @Override
			public void getRole(AccessibleControlEvent e) {
                e.detail = ACC.ROLE_COMBOBOX;
            }

            @Override
			public void getState(AccessibleControlEvent e) {
                e.detail = ACC.STATE_NORMAL;
            }

            @Override
			public void getValue(AccessibleControlEvent e) {
                e.result = textViewer.getTextWidget().getText();
            }
        });
}

private void internalLayout() {
    if (isDropped()) {
        dropDown(false);
    }

    Rectangle rect = getClientArea();
    int width = rect.width;
    int height = rect.height;
    Point arrowSize = arrow.computeSize(SWT.DEFAULT, height);
    textViewer.getTextWidget().setBounds(0, 0, width - arrowSize.x, height);
    arrow.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);

    Point size = getSize();
    int itemHeight = dp.getBounds().height;
    Point listSize = dp.computeSize(SWT.DEFAULT, itemHeight);
    dp.setBounds(1, 1, Math.max(size.x - 2, listSize.x), listSize.y+20);
}

public boolean isClosePopupWithSingleMouseClick() {
    return isClosePopupWithSingleMouseClick;
}

private boolean isDropped() {
    return popup.getVisible();
}

@Override
public boolean isFocusControl() {
    checkWidget();

    if (textViewer.getTextWidget().isFocusControl() || arrow.isFocusControl()
            || dp.isFocusControl() || popup.isFocusControl()) {
        return true;
    } else {
        return super.isFocusControl();
    }
}

private void popupEvent(Event event) {
    switch (event.type) {
    case SWT.Paint:

        // draw black rectangle around dp
        Rectangle listRect = dp.getBounds();
        Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
        event.gc.setForeground(black);
        event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height
            + 1);

        break;

    case SWT.Close:
        event.doit = false;
        dropDown(false);

        break;

    case SWT.Deactivate:
        dropDown(false);

        break;
    }
}

@Override
public void redraw(int x, int y, int width, int height, boolean all) {
    checkWidget();

    if (!all) {
        return;
    }

    Point location = textViewer.getTextWidget().getLocation();
    textViewer.getTextWidget().redraw(x - location.x, y - location.y, width, height, all);
    location = dp.getLocation();
    dp.redraw(x - location.x, y - location.y, width, height, all);

    if (arrow != null) {
        location = arrow.getLocation();
        arrow.redraw(x - location.x, y - location.y, width, height, all);
    }
}

public void removeModifyListener(ModifyListener listener) {
    checkWidget();

    if (listener == null) {
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }

    removeListener(SWT.Modify, listener);
}

public void removeSelectionListener(SelectionListener listener) {
    checkWidget();

    if (listener == null) {
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }

    removeListener(SWT.Selection, listener);
    removeListener(SWT.DefaultSelection, listener);
}

@Override
public void setBackground(Color color) {
    super.setBackground(color);

    if (textViewer.getTextWidget() != null) {
        textViewer.getTextWidget().setBackground(color);
    }

    if (dp != null) {
        dp.setBackground(color);
    }

    if (arrow != null) {
        arrow.setBackground(color);
    }
}

public void setClosePopupWithSingleMouseClick(
    boolean isClosePopupWithSingleMouseClick) {
    this.isClosePopupWithSingleMouseClick = isClosePopupWithSingleMouseClick;
}

public void setFormat(DateFormat format) {
	this.format = format;
}

protected DateFormat getFormat() {
	if(format==null) format = DateFormat.getDateInstance(DateFormat.SHORT);
	return format;
}

public void setDate(Date date) {
    checkWidget();

    if (date != null) {
        textViewer.getDocument().set(getFormat().format(date));
        //textViewer.getTextWidget().selectAll();
    } else {
        textViewer.getDocument().set("");
    }

    dp.setDate(date);
}

@Override
public boolean setFocus() {
    checkWidget();

    return textViewer.getTextWidget().setFocus();
}

@Override
public void setFont(Font font) {
    super.setFont(font);
    textViewer.getTextWidget().setFont(font);
    dp.setFont(font);
    internalLayout();
}

@Override
public void setForeground(Color color) {
    super.setForeground(color);

    if (textViewer.getTextWidget() != null) {
        textViewer.getTextWidget().setForeground(color);
    }

    if (dp != null) {
        dp.setForeground(color);
    }

    if (arrow != null) {
        arrow.setForeground(color);
    }
}

public void setSelection(Point selection) {
    checkWidget();

    if (selection == null) {
        SWT.error(SWT.ERROR_NULL_ARGUMENT);
    }

    textViewer.getTextWidget().setSelection(selection.x, selection.y);
}

public void setTextLimit(int limit) {
    checkWidget();
    textViewer.getTextWidget().setTextLimit(limit);
}

@Override
public void setToolTipText(String string) {
    checkWidget();
    super.setToolTipText(string);
    arrow.setToolTipText(string);
    textViewer.getTextWidget().setToolTipText(string);
}

/**
 * Set up visibility of control
 *
 * @param visible visibility state
 */
@Override
public void setVisible(boolean visible) {
    super.setVisible(visible);

    if (!visible) {
        popup.setVisible(false);
    }
}

private void textEvent(Event event) {
    switch (event.type) {
    case SWT.FocusIn: {
        if (hasFocus) {
            return;
        }

        hasFocus = true;

        if (getEditable()) {
            //textViewer.getTextWidget().selectAll();
        }

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.FocusIn, e);

        break;
    }

    case SWT.FocusOut: {
        Control focusControl = getDisplay().getFocusControl();

        if ((focusControl == dp) || (focusControl == arrow)) {
            return;
        }

        hasFocus = false;

        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.FocusOut, e);

        break;
    }

    case SWT.KeyDown: {
        if (event.character == SWT.ESC) {
            // escape key cancels popup dp
            dropDown(false);
        }

        if (event.character == SWT.CR) {
            dropDown(false);

            Event e = new Event();
            e.time = event.time;
            e.stateMask = event.stateMask;
            notifyListeners(SWT.DefaultSelection, e);
        }

        //At this point the widget may have been disposed.
        // If so, do not continue.
        if (isDisposed()) {
            break;
        }

        if ((event.keyCode == SWT.ARROW_UP)
                || (event.keyCode == SWT.ARROW_DOWN)) {
            //Date oldDate = getDate();
            //At this point the widget may have been disposed.
            // If so, do not continue.
            if (isDisposed()) {
                break;
            }
        }

        // Further work : Need to add support for incremental
        // search in pop up dp as characters typed in text widget
        Event e = new Event();
        e.time = event.time;
        e.character = event.character;
        e.keyCode = event.keyCode;
        e.stateMask = event.stateMask;
        notifyListeners(SWT.KeyDown, e);

        break;
    }

    case SWT.KeyUp: {
        Event e = new Event();
        e.time = event.time;
        e.character = event.character;
        e.keyCode = event.keyCode;
        e.stateMask = event.stateMask;
        notifyListeners(SWT.KeyUp, e);

        break;
    }

    case SWT.Modify: {
        // sebthom
        if (!popup.isVisible()) {
            if (textViewer.getTextWidget().getText().length() == 0) {
                dp.setDate(null);
            } else {
                try {
                    dp.setDate(getFormat().parse(textViewer.getTextWidget()
                            .getText()));
                } catch (ParseException pe) {
                    dp.setDate(null);
                }
            }
        }

        //	dp.deselectAll ();
        Event e = new Event();
        e.time = event.time;
        notifyListeners(SWT.Modify, e);

        break;
    }

    case SWT.MouseDown: {
        if ((event.button != 1) || textViewer.getTextWidget().getEditable()) {
            return;
        }

        boolean dropped = isDropped();
        //textViewer.getTextWidget().selectAll();

        if (!dropped) {
            setFocus();
        }

        dropDown(!dropped);

        break;
    }

    case SWT.MouseUp: {
        if ((event.button != 1) || textViewer.getTextWidget().getEditable()) {
            return;
        }

        //textViewer.getTextWidget().selectAll();

        break;
    }

    case SWT.Traverse: {
        switch (event.detail) {
        case SWT.TRAVERSE_RETURN:
        case SWT.TRAVERSE_ARROW_PREVIOUS:
        case SWT.TRAVERSE_ARROW_NEXT:

            // The enter causes default selection and
            // the arrow keys are used to manipulate the dp
            // contents so do not use them for traversal.
            event.doit = false;

            break;
        }

        Event e = new Event();
        e.time = event.time;
        e.detail = event.detail;
        e.doit = event.doit;
        e.keyCode = event.keyCode;
        notifyListeners(SWT.Traverse, e);
        event.doit = e.doit;

        break;
    }
    }
}

/**
 * Enable/disable widget
 *
 * @author andyglow
 * @param enabled
 */
@Override
public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    textViewer.getTextWidget().setEnabled(enabled);
    dp.setEnabled(enabled);
    arrow.setEnabled(enabled);
}
}
