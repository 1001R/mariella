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

public interface ControlFactory {

public Button createButton(Composite parent, String text, int style);

public Composite createComposite(Composite parent, int style);

public Composite createComposite(Composite parent);

public Composite createCompositeSeparator(Composite parent);

public ExpandableComposite createExpandableComposite(Composite parent, int expansionStyle);

public Hyperlink createHyperlink(Composite parent, String text, int style);

public ImageHyperlink createImageHyperlink(Composite parent, int style);

public Label createLabel(Composite parent, String text, int style);

public Label createLabel(Composite parent, String text);

public Label createSeparator(Composite parent, int style);

public Table createTable(Composite parent, int style);

public Text createText(Composite parent, String value, int style);

public Text createText(Composite parent, String value);

public Tree createTree(Composite parent, int style);

public TextViewer createTextViewer(Composite client, int style);

public ComboViewer createComboViewer(Composite client, int style);


}
