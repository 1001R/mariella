package org.mariella.rcp.databinding.internal;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mariella.rcp.databinding.VBindingContext;


public class RcpObservables {

public static ISWTObservableValue observeText(VBindingContext ctx, TextViewer textViewer, int eventType) {
	if (textViewer.getDocument() == null)
		textViewer.setDocument(new Document());
	// overload tab processing from text viewers (yes, we want traverse behaviour on TAB-key events)
	textViewer.getTextWidget().addTraverseListener(new TraverseListener() {
		public void keyTraversed(TraverseEvent e) {
			if ('\t' == e.character)
				e.doit= true;
		}
	});
	VTextViewerObservableValue value = new VTextViewerObservableValue(ctx, textViewer, eventType);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeText(VBindingContext ctx, Text text, int eventType) {
	ISWTObservableValue value = new VTextObservableValue(text, eventType);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeLabel(VBindingContext ctx, Label label) {
	ISWTObservableValue value = new LabelObservableValue(label);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeControlVisible(VBindingContext ctx, Control control, Composite parentToRedraw) {
	return new ControlVisibleObservableValue(control, parentToRedraw);
}

public static ISWTObservableValue observeDateTime(VBindingContext ctx, DateTime dateTime) {
	ISWTObservableValue value = new DateTimeObservableValue(dateTime);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeSingleSelection(VBindingContext ctx, StructuredViewer structuredViewer) {
	IObservableValue value = new VStructuredViewerSingleSelectionObservableValue(ViewersObservables.observeSingleSelection(structuredViewer), structuredViewer);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeSingleSelection(VBindingContext ctx, StructuredViewer structuredViewer, Class targetType) {
	IObservableValue value = new VStructuredViewerSingleSelectionObservableValue(ViewersObservables.observeSingleSelection(structuredViewer), structuredViewer, targetType);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeComboViewer(VBindingContext ctx, ComboViewer comboViewer) {
	IObservableValue value = new VComboViewerObservableValue(ViewersObservables.observeSingleSelection(comboViewer), comboViewer);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeComboViewer(VBindingContext ctx, ComboViewer comboViewer, Class targetType) {
	IObservableValue value = new VComboViewerObservableValue(ViewersObservables.observeSingleSelection(comboViewer), comboViewer, targetType);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static IObservableList observeTableViewer(VBindingContext ctx, TableViewer tableViewer, Class type) {
	IObservableList value = new VTableViewerObservableList(ctx, tableViewer, type);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeButton(VBindingContext ctx, Button button) {
	ISWTObservableValue value = new VButtonObservableValue(button);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static ISWTObservableValue observeRadioButton(VBindingContext ctx, Button button, Object matchingValue) {
	ISWTObservableValue value = new VRadioButtonObservableValue(button, matchingValue);
	ctx.observablesManager.addObservable(value);
	return value;
}

public static IObservableValue observeAction(VBindingContext ctx, Action action) {
	IObservableValue value = new VActionObservable(action);
	ctx.observablesManager.addObservable(value);
	return value;
}

}
