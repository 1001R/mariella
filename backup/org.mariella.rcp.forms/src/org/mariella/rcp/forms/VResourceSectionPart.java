package org.mariella.rcp.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class VResourceSectionPart extends SectionPart {

VResourceFormPage page;

public VResourceSectionPart(VResourceFormPage page, Composite parent, FormToolkit toolkit, int style) {
	super(parent, toolkit, style);
	this.page = page;
	page.resourceSectionParts.add(this);
	if (getSection().getDescriptionControl() != null)
		getSection().getDescriptionControl().setEnabled(false);
}

public VFormToolkit getVFormToolkit() {
	return ((AbstractVResourceFormEditor)page.getEditor()).getVFormToolkit();
}

public VResourceFormPage getPage() {
	return page;
}

}
