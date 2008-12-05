package org.mariella.glue.ui;

import java.text.SimpleDateFormat;

import org.mariella.glue.service.Context;
import org.mariella.glue.service.LostUpdateEntity;
import org.mariella.persistence.runtime.Modifiable;


public class LostUpdateEntityAdapter <T extends LostUpdateEntity> extends EntityAdapter<T> {
	private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	private String createText;
	private String updateText;
	
public LostUpdateEntityAdapter(Context context, T entity) {
	super(context, entity);
}

public String getCreateText() {
	if(createText == null) {
		createText = "created by " + entity.getCreateUser() + " at " + format.format(entity.getCreateTimestamp());
	}
	return createText;
}

public String getUpdateText() {
	if(updateText == null) {
		if(entity.getUpdateUser() != null && entity.getUpdateTimestamp() != null) {
			updateText = "last modified by " + entity.getUpdateUser() + " at " + format.format(entity.getUpdateTimestamp());
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
