package org.mariella.glue.service;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.ColumnMapping;
import at.hts.persistence.runtime.Modifiable;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.ScalarPropertyDescription;
import at.hts.persistence.schema.SchemaDescription;

public class Entity implements Modifiable {
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private Long id;
	private long creation;
	
public Entity() {
	creation = System.currentTimeMillis();
}

public PropertyChangeSupport getPropertyChangeSupport() {
	return propertyChangeSupport;
}

public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.addPropertyChangeListener(listener);
}

public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeSupport.removePropertyChangeListener(listener);
}

public Long getIdentity() {
	return id;
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	Long oldId = this.id;
	this.id = id;
	propertyChangeSupport.firePropertyChange("id", oldId, id);
}

public long getCreation() {
	return creation;
}

public static void createDescription(SchemaDescription schemaDescription) {
	ClassDescription cd = new ClassDescription(
									schemaDescription, 
									Entity.class.getName()
							);
	PropertyDescription pd = new ScalarPropertyDescription(cd, SchemaDescription.getPropertyDescriptor(Entity.class, "id"));
	cd.addPropertyDescription(pd);
	
	cd.setId(pd);
	
	schemaDescription.addClassDescription(cd);
}

public static void updateMapping(ClassMapping classMapping) {
	ClassDescription cd = classMapping.getClassDescription();

	PropertyDescription pd;

	pd = cd.getPropertyDescription("id");
	classMapping.setPropertyMapping(pd, new ColumnMapping(classMapping, pd, "ID"));
}

}
