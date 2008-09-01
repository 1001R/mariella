package org.mariella.glue.service;

import java.sql.Timestamp;

import at.hts.persistence.mapping.ClassMapping;
import at.hts.persistence.mapping.ColumnMapping;
import at.hts.persistence.schema.ClassDescription;
import at.hts.persistence.schema.PropertyDescription;
import at.hts.persistence.schema.ScalarPropertyDescription;
import at.hts.persistence.schema.SchemaDescription;

public class LostUpdateEntity extends Entity {
	private String createUser;
	private Timestamp createTimestamp;
	private String updateUser;
	private Timestamp updateTimestamp;
	
public String getCreateUser() {
	return createUser;
}

public void setCreateUser(String createUser) {
	String old = this.createUser;
	this.createUser = createUser;
	propertyChangeSupport.firePropertyChange("createUser", old, createUser);
}

public Timestamp getCreateTimestamp() {
	return createTimestamp;
}

public void setCreateTimestamp(Timestamp createTimestamp) {
	Timestamp old = this.createTimestamp;
	this.createTimestamp = createTimestamp;
	propertyChangeSupport.firePropertyChange("createTimestamp", old, createTimestamp);
}

public String getUpdateUser() {
	return updateUser;
}

public void setUpdateUser(String updateUser) {
	String old = this.updateUser;
	this.updateUser = updateUser;
	propertyChangeSupport.firePropertyChange("updateUser", old, updateUser);
}

public Timestamp getUpdateTimestamp() {
	return updateTimestamp;
}

public void setUpdateTimestamp(Timestamp updateTimestamp) {
	Timestamp old = this.updateTimestamp;
	this.updateTimestamp = updateTimestamp;
	propertyChangeSupport.firePropertyChange("updateTimestamp", old, updateTimestamp);
}

public static void createDescription(SchemaDescription schemaDescription) {
	ClassDescription cd = new ClassDescription(
									schemaDescription, 
									schemaDescription.getClassDescription(Entity.class.getName()),
									LostUpdateEntity.class.getName()
							);
	PropertyDescription pd;
	
	pd = new ScalarPropertyDescription(cd, SchemaDescription.getPropertyDescriptor(LostUpdateEntity.class, "createUser"));
	cd.addPropertyDescription(pd);
	pd = new ScalarPropertyDescription(cd, SchemaDescription.getPropertyDescriptor(LostUpdateEntity.class, "createTimestamp"));
	cd.addPropertyDescription(pd);
	pd = new ScalarPropertyDescription(cd, SchemaDescription.getPropertyDescriptor(LostUpdateEntity.class, "updateUser"));
	cd.addPropertyDescription(pd);
	pd = new ScalarPropertyDescription(cd, SchemaDescription.getPropertyDescriptor(LostUpdateEntity.class, "updateTimestamp"));
	cd.addPropertyDescription(pd);

	schemaDescription.addClassDescription(cd);
}

public static void updateMapping(ClassMapping classMapping) {
	Entity.updateMapping(classMapping);
	
	ClassDescription cd = classMapping.getClassDescription();
	PropertyDescription pd;

	pd = cd.getPropertyDescription("createUser");
	classMapping.setPropertyMapping(pd, new ColumnMapping(classMapping, pd, "CREATE_USER"));

	pd = cd.getPropertyDescription("createTimestamp");
	classMapping.setPropertyMapping(pd, new ColumnMapping(classMapping, pd, "CREATE_TIMESTAMP"));

	pd = cd.getPropertyDescription("updateUser");
	classMapping.setPropertyMapping(pd, new ColumnMapping(classMapping, pd, "UPDATE_USER"));

	pd = cd.getPropertyDescription("updateTimestamp");
	classMapping.setPropertyMapping(pd, new ColumnMapping(classMapping, pd, "UPDATE_TIMESTAMP"));
}

}
