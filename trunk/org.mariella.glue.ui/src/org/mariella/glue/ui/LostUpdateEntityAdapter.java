package org.mariella.glue.ui;

import org.mariella.glue.service.Context;
import org.mariella.glue.service.LostUpdateEntity;

import at.hts.persistence.runtime.Modifiable;

public class LostUpdateEntityAdapter <T extends LostUpdateEntity> extends EntityAdapter<T> {
	private String createText;
	private String updateText;
	
public LostUpdateEntityAdapter(Context context, T entity) {
	super(context, entity);
}

public String getCreateText() {
	if(createText == null) {
		createText = "created by " + entity.getCreateUser() + " at " + entity.getCreateTimestamp().toString();
	}
	return createText;
}

public String getUpdateText() {
	if(updateText == null) {
		if(entity.getUpdateUser() != null && entity.getUpdateTimestamp() != null) {
			updateText = "last modified by " + entity.getUpdateUser() + " at " + entity.getUpdateTimestamp().toString();
		} else {
			updateText = "no modifications after creation";
		}
	}
	return updateText;
}

@Override
public void propertyChanged(Modifiable modifiable, String propertyName, Object oldValue, Object newValue) {
	super.propertyChanged(modifiable, propertyName, oldValue, newValue);
	if(propertyName.equals("createUser") || propertyName.equals("createTimestamp")) {
		String old = createText;
		createText = null;
		propertyChangeSupport.firePropertyChange("createText", old, getCreateText());
	} else if(propertyName.equals("updateUser") || propertyName.equals("updateTimestamp")) {
		String old = updateText;
		updateText = null;
		propertyChangeSupport.firePropertyChange("updateText", old, getUpdateText());
	} 
}

}
