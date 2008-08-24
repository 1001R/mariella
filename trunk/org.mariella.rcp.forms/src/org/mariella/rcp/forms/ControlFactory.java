package org.mariella.rcp.forms;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

public interface ControlFactory {

Button createButton(Composite parent, String text, int style);

Composite createComposite(Composite parent, int style);

Composite createComposite(Composite parent);

Composite createCompositeSeparator(Composite parent);

ExpandableComposite createExpandableComposite(Composite parent, int expansionStyle);

Hyperlink createHyperlink(Composite parent, String text, int style);

ImageHyperlink createImageHyperlink(Composite parent, int style);

Label createLabel(Composite parent, String text, int style);

Label createLabel(Composite parent, String text);

Label createSeparator(Composite parent, int style);

Table createTable(Composite parent, int style);

Text createText(Composite parent, String value, int style);

Text createText(Composite parent, String value);

Tree createTree(Composite parent, int style);

TextViewer createTextViewer(Composite client, int style);

ComboViewer createComboViewer(Composite client, int style);

Section createSection(Composite parent, int sectionStyle);

TextViewer createTextViewer(Composite client, int single, boolean b);


}
