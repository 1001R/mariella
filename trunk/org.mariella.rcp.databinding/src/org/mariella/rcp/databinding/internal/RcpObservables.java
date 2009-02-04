package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mariella.rcp.databinding.VBindingContext;


public class RcpObservables {

public static ISWTObservableValue observeText(VBindingContext ctx, TextViewer textViewer, int eventType, int traverseEventType) {
	if (textViewer.getDocument() == null)
		textViewer.setDocument(new Document());
	// overload tab processing from text viewers (yes, we want traverse behaviour on TAB-key events)
	textViewer.getTextWidget().addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if ('\t' == e.character)
				e.doit= true;
		}
	});
	VTextViewerObservableValue value = new VTextViewerObservableValue(ctx, textViewer, eventType, traverseEventType);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeText(VBindingContext ctx, Text text, int eventType, int traverseEventType) {
	ISWTObservableValue value = new VTextObservableValue(text, eventType, traverseEventType);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeLabel(VBindingContext ctx, Label label) {
	ISWTObservableValue value = new LabelObservableValue(label);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeControlVisible(VBindingContext ctx, Control control, Composite parentToRedraw) {
	return new ControlVisibleObservableValue(control, parentToRedraw);
}

public static ISWTObservableValue observeControlEnabled(VBindingContext ctx, Control control) {
	return new ControlEnabledObservableValue(control);
}

public static ISWTObservableValue observeDateTime(VBindingContext ctx, DateTime dateTime) {
	ISWTObservableValue value = new DateTimeObservableValue(dateTime);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeSingleSelection(VBindingContext ctx, StructuredViewer structuredViewer) {
	IObservableValue value = new VStructuredViewerSelectionObservableValue(ViewersObservables.observeSingleSelection(structuredViewer), structuredViewer);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeSingleSelection(VBindingContext ctx, StructuredViewer structuredViewer, Class targetType) {
	IObservableValue value = new VStructuredViewerSelectionObservableValue(ViewersObservables.observeSingleSelection(structuredViewer), structuredViewer, targetType);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeMultiSelection(VBindingContext ctx, StructuredViewer structuredViewer) {
	IObservableValue value = new VStructuredViewerSelectionObservableValue(
			new SelectionProviderMultiSelectionObservableValue(structuredViewer),
			structuredViewer);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeMultiSelection(VBindingContext ctx, StructuredViewer structuredViewer, Class targetType) {
	IObservableValue value = new VStructuredViewerSelectionObservableValue(
			new SelectionProviderMultiSelectionObservableValue(structuredViewer),
			structuredViewer,
			targetType);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableList observeTableViewer(VBindingContext ctx, TableViewer tableViewer, Class type) {
	IObservableList value = new VTableViewerObservableList(ctx, tableViewer, type);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableList observeListViewer(VBindingContext ctx, AbstractListViewer listViewer, Class type) {
	IObservableList value = new VListViewerObservableList(ctx, listViewer, type);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeButton(VBindingContext ctx, Button button) {
	ISWTObservableValue value = new VButtonObservableValue(button);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeRadioButton(VBindingContext ctx, Button button, Object matchingValue) {
	ISWTObservableValue value = new VRadioButtonObservableValue(button, matchingValue);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeAction(VBindingContext ctx, Action action) {
	IObservableValue value = new VActionObservable(action);
	((InternalBindingContext)ctx).getMainContext().observablesManager.addObservable(value);
	return value;
}

}
