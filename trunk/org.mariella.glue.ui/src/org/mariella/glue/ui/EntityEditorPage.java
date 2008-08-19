package org.mariella.glue.ui;

import org.mariella.glue.service.Entity;
import org.mariella.rcp.databinding.AbstractEnabledCallback;
import org.mariella.rcp.databinding.EnabledCallback;
import org.mariella.rcp.forms.VResourceFormPage;



public abstract class EntityEditorPage <T extends Entity> extends VResourceFormPage {
	
public EntityEditorPage(EntityEditor<T> editor, String id, String label) {
	super(editor, id, label);
}

@Override
@SuppressWarnings("unchecked")
public EntityEditor<T> getEditor() {
	return (EntityEditor<T>)super.getEditor();
}

public EnabledCallback getGeneralEnabledCallback() {
	return new AbstractEnabledCallback() {
		public boolean isEnabled() {
			return true;
		}
	};
}

}
