package org.mariella.rcp.forms;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
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
int fontSizeDelta = 0;

public VFormToolkit(FormToolkit formToolkit) {
	this.formToolkit = formToolkit;
}

public VFormToolkit(FormToolkit formToolkit, int fontSizeDelta) {
	this.formToolkit = formToolkit;
	this.fontSizeDelta = fontSizeDelta;
}

public void adapt(Composite composite) {
	formToolkit.adapt(composite);
	handleFont(composite);
}

public void adapt(Control control, boolean trackFocus, boolean trackKeyboard) {
	formToolkit.adapt(control, trackFocus, trackKeyboard);
	handleFont(control);
}

public Button createButton(Composite parent, String text, int style) {
	Button button = formToolkit.createButton(parent, text, style);
	return button;
}

public Composite createComposite(Composite parent, int style) {
	Composite composite = formToolkit.createComposite(parent, style);
	formToolkit.paintBordersFor(composite);
	handleFont(composite);
	return composite;
}

public Composite createComposite(Composite parent) {
	return createComposite(parent, SWT.NONE);
}

public Composite createCompositeSeparator(Composite parent) {
	Composite composite = formToolkit.createCompositeSeparator(parent);
	handleFont(composite);
	return composite;
}

public ExpandableComposite createExpandableComposite(Composite parent, int expansionStyle) {
	ExpandableComposite composite = formToolkit.createExpandableComposite(parent, expansionStyle);
	handleFont(composite);
	return composite;
}

public Form createForm(Composite parent) {
	Form form = formToolkit.createForm(parent);
	handleFont(form);
	return form;
}

public FormText createFormText(Composite parent, boolean trackFocus) {
	FormText t = formToolkit.createFormText(parent, trackFocus);
	handleFont(t);
	return t;
}

public Hyperlink createHyperlink(Composite parent, String text, int style) {
	Hyperlink link = formToolkit.createHyperlink(parent, text, style);
	handleFont(link);
	return link;
}

public ImageHyperlink createImageHyperlink(Composite parent, int style) {
	ImageHyperlink link = formToolkit.createImageHyperlink(parent, style);
	handleFont(link);
	return link;
}

public Label createLabel(Composite parent, String text, int style) {
	Label label = formToolkit.createLabel(parent, text, style);
	label.setForeground(formToolkit.getColors().getColor(IFormColors.TITLE));
	handleFont(label);
	return label;
}

public Label createLabel(Composite parent, String text) {
	return createLabel(parent, text, SWT.NONE);
}

public ScrolledPageBook createPageBook(Composite parent, int style) {
	ScrolledPageBook book = formToolkit.createPageBook(parent, style);
	handleFont(book);
	return book;
}

public ScrolledForm createScrolledForm(Composite parent) {
	ScrolledForm form = formToolkit.createScrolledForm(parent);
	handleFont(form);
	return form;
}

@Override
public Section createSection(Composite parent, int sectionStyle) {
	Section section = formToolkit.createSection(parent, sectionStyle);
	formToolkit.paintBordersFor(section);
	handleFont(section);
	return section;
}

public Label createSeparator(Composite parent, int style) {
	Label label = formToolkit.createSeparator(parent, style);
	return label;
}

public Table createTable(Composite parent, int style) {
	Table table = formToolkit.createTable(parent, style);
	table.setLinesVisible(true);
	handleFont(table);
	return table;
}

public Text createText(Composite parent, String value, int style) {
	Text text = formToolkit.createText(parent, value, style);
	text.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
	handleFont(text);
	return text;
}

public Text createText(Composite parent, String value) {
	Text text = formToolkit.createText(parent, value, SWT.NONE);
	handleFont(text);
	return text;
}

public Tree createTree(Composite parent, int style) {
	Tree tree = formToolkit.createTree(parent, style);
	handleFont(tree);
	return tree;
}

public void decorateFormHeading(Form form) {
	formToolkit.decorateFormHeading(form);
}

public void dispose() {
	formToolkit.dispose();
}

@Override
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

@Override
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

@Override
public String toString() {
	return formToolkit.toString();
}

public TextViewer createTextViewer(Composite client, int style) {
	return createTextViewer(client, style, true);
}

public TextViewer createTextViewer(Composite client, int style, boolean drawBorder) {
	if ((style & SWT.BORDER) != 0) {
		style = style ^ SWT.BORDER;
	}
	
	final TextViewer textViewer = new TextViewer(client, style);
	if (drawBorder)
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
	handleFont(textViewer.getControl());
	return textViewer;
}

public ComboViewer createComboViewer(Composite parent, int style) {
	final ComboViewer comboViewer = new ComboViewer(parent, style);
	handleFont(comboViewer.getControl());
	return comboViewer;
}


private void handleFont(Control control) {
	Font font = new FontToolkit().deriveFont(control.getFont(), fontSizeDelta, SWT.NONE);
	control.setFont(font);
}
}
