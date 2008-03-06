package org.mariella.rcp.forms;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.HyperlinkGroup;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.ScrolledPageBook;
import org.eclipse.ui.forms.widgets.Section;

public class VFormToolkit implements ControlFactory {

FormToolkit formToolkit;

VFormToolkit(FormToolkit formToolkit) {
	this.formToolkit = formToolkit;
}

public void adapt(Composite composite) {
	formToolkit.adapt(composite);
}

public void adapt(Control control, boolean trackFocus, boolean trackKeyboard) {
	formToolkit.adapt(control, trackFocus, trackKeyboard);
}

public Button createButton(Composite parent, String text, int style) {
	return formToolkit.createButton(parent, text, style);
}

public Composite createComposite(Composite parent, int style) {
	Composite composite = formToolkit.createComposite(parent, style);
	formToolkit.paintBordersFor(composite);
	return composite;
}

public Composite createComposite(Composite parent) {
	return createComposite(parent, SWT.NONE);
}

public Composite createCompositeSeparator(Composite parent) {
	return formToolkit.createCompositeSeparator(parent);
}

public ExpandableComposite createExpandableComposite(Composite parent, int expansionStyle) {
	return formToolkit.createExpandableComposite(parent, expansionStyle);
}

public Form createForm(Composite parent) {
	return formToolkit.createForm(parent);
}

public FormText createFormText(Composite parent, boolean trackFocus) {
	return formToolkit.createFormText(parent, trackFocus);
}

public Hyperlink createHyperlink(Composite parent, String text, int style) {
	return formToolkit.createHyperlink(parent, text, style);
}

public ImageHyperlink createImageHyperlink(Composite parent, int style) {
	return formToolkit.createImageHyperlink(parent, style);
}

public Label createLabel(Composite parent, String text, int style) {
	Label label = formToolkit.createLabel(parent, text, style);
	label.setForeground(formToolkit.getColors().getColor(IFormColors.TITLE));
	return label;
}

public Label createLabel(Composite parent, String text) {
	return createLabel(parent, text, SWT.NONE);
}

public ScrolledPageBook createPageBook(Composite parent, int style) {
	return formToolkit.createPageBook(parent, style);
}

public ScrolledForm createScrolledForm(Composite parent) {
	return formToolkit.createScrolledForm(parent);
}

public Section createSection(Composite parent, int sectionStyle) {
	Section section = formToolkit.createSection(parent, sectionStyle);
	formToolkit.paintBordersFor(section);
	return section;
}

public Label createSeparator(Composite parent, int style) {
	return formToolkit.createSeparator(parent, style);
}

public Table createTable(Composite parent, int style) {
	Table table = formToolkit.createTable(parent, style);
	table.setLinesVisible(true);
	return table;
}

public Text createText(Composite parent, String value, int style) {
	Text text = formToolkit.createText(parent, value, style);
	text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
	return text;
}

public Text createText(Composite parent, String value) {
	return formToolkit.createText(parent, value, SWT.NONE);
}

public Tree createTree(Composite parent, int style) {
	return formToolkit.createTree(parent, style);
}

public void decorateFormHeading(Form form) {
	formToolkit.decorateFormHeading(form);
}

public void dispose() {
	formToolkit.dispose();
}

public boolean equals(Object obj) {
	return formToolkit.equals(obj);
}

public int getBorderMargin() {
	return formToolkit.getBorderMargin();
}

public int getBorderStyle() {
	return formToolkit.getBorderStyle();
}

public FormColors getColors() {
	return formToolkit.getColors();
}

public HyperlinkGroup getHyperlinkGroup() {
	return formToolkit.getHyperlinkGroup();
}

public int getOrientation() {
	return formToolkit.getOrientation();
}

public int hashCode() {
	return formToolkit.hashCode();
}

public void paintBordersFor(Composite parent) {
	formToolkit.paintBordersFor(parent);
}

public void refreshHyperlinkColors() {
	formToolkit.refreshHyperlinkColors();
}

public void setBackground(Color bg) {
	formToolkit.setBackground(bg);
}

public void setBorderStyle(int style) {
	formToolkit.setBorderStyle(style);
}

public void setOrientation(int orientation) {
	formToolkit.setOrientation(orientation);
}

public String toString() {
	return formToolkit.toString();
}

public TextViewer createTextViewer(Composite client, int style) {
	final TextViewer textViewer = new TextViewer(client, SWT.NONE);
	textViewer.getTextWidget().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
	Document doc = new Document();
	doc.addDocumentListener(new IDocumentListener() {
		public void documentChanged(DocumentEvent event) {
			textViewer.getTextWidget().setLineIndent(0, event.getDocument().getNumberOfLines(), 3);
		}
		public void documentAboutToBeChanged(DocumentEvent event) {
		}
	});
	textViewer.setDocument(doc);
	return textViewer;
}

public ComboViewer createComboViewer(Composite parent, int style) {
	final ComboViewer comboViewer = new ComboViewer(parent, style);
	return comboViewer;
}

}