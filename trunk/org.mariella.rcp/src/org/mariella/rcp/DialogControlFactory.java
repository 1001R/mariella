package org.mariella.rcp;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.mariella.rcp.ControlFactory;

public class DialogControlFactory implements ControlFactory {

public Button createButton(Composite parent, String text, int style) {
	Button button = new Button(parent, style);
	button.setText(text);
	return button;
}

public Composite createComposite(Composite parent, int style) {
	return new Composite(parent, style);
}

public Composite createComposite(Composite parent) {
	return createComposite(parent, SWT.NONE);
}

public Composite createCompositeSeparator(Composite parent) {
	return createComposite(parent);
}

public ExpandableComposite createExpandableComposite(Composite parent, int expansionStyle) {
	return new ExpandableComposite(parent, expansionStyle);
}

public Hyperlink createHyperlink(Composite parent, String text, int style) {
	Hyperlink link = new Hyperlink(parent, style);
	link.setText(text);
	return link;
}

public ImageHyperlink createImageHyperlink(Composite parent, int style) {
	ImageHyperlink link = new ImageHyperlink(parent, style);
	return link;
}

public Label createLabel(Composite parent, String text, int style) {
	Label label = new Label(parent, style);
	label.setText(text);
	return label;
}

public Label createLabel(Composite parent, String text) {
	return createLabel(parent, text, SWT.NONE);
}

public Label createSeparator(Composite parent, int style) {
	Label label = new Label(parent, SWT.SEPARATOR | style);
	return label;
}

public Table createTable(Composite parent, int style) {
	Table table = new Table(parent, style);
	table.setLinesVisible(true);
	new TableColumnWidthHandler().handleColumnWidths(table);
	return table;
}

public Text createText(Composite parent, String value, int style) {
	Text text = new Text(parent, style);
	text.setText(value);
	return text;
}

public Text createText(Composite parent, String value) {
	return createText(parent, value, SWT.BORDER);
}

public TextViewer createTextViewer(Composite parent, int style, boolean dontPaintBorders) {
	final TextViewer textViewer = new TextViewer(parent, style);
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
public TextViewer createTextViewer(Composite parent, int style) {
	return createTextViewer(parent, style, false);
}

public ComboViewer createComboViewer(Composite parent, int style) {
	final ComboViewer comboViewer = new ComboViewer(parent, style);
	return comboViewer;
}

public Tree createTree(Composite parent, int style) {
	return new Tree(parent, style);
}

@Override
public Group createGroup(Composite composite, String text) {
	Group group = new Group(composite, SWT.NONE);
	group.setText(text);
	return group;
}

@Override
public Section createSection(Composite parent, int sectionStyle) {
	Section section = new Section(parent, sectionStyle);
	return section;
}

@Override
public void adapt(Composite composite) {
}
}
